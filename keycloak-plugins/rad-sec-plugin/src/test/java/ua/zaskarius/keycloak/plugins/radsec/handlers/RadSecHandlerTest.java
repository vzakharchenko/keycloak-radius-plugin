package ua.zaskarius.keycloak.plugins.radsec.handlers;

import io.netty.channel.ChannelHandlerContext;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.server.RequestCtx;
import org.tinyradius.util.RadiusEndpoint;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAccountHandlerProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAuthHandlerProvider;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.net.InetSocketAddress;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static ua.zaskarius.keycloak.plugins.radsec.handlers.RadSecHandler.DEFAULT_RADSEC_RADIUS_PROVIDER;

public class RadSecHandlerTest extends AbstractRadiusTest {
    private RadSecHandler radSecHandler = new RadSecHandler();
    @Mock
    private ChannelHandlerContext ctx;


    @BeforeMethod
    public void beforeMethods() {
        reset(ctx);
    }

    @Test
    public void testMethods() {
        assertEquals(radSecHandler.create(session), radSecHandler);
        assertEquals(radSecHandler.acceptedPacketType(), RadiusPacket.class);
        assertEquals(radSecHandler.getChannelHandler(session), radSecHandler);
        radSecHandler.close();
        radSecHandler.init(null);
        radSecHandler.postInit(null);
        assertEquals(radSecHandler.getId(), DEFAULT_RADSEC_RADIUS_PROVIDER);
    }


    @Test
    public void testAccessPackage() {
        RequestCtx requestCtx = new RequestCtx(new AccessRequest(realDictionary, 1, new byte[16]),
                new RadiusEndpoint(new InetSocketAddress(0), "test"));
        radSecHandler.create(session);
        radSecHandler.channelRead0(ctx, requestCtx);
        IRadiusAuthHandlerProvider provider = session
                .getProvider(IRadiusAuthHandlerProvider.class);
        verify(provider).directRead(ctx, requestCtx);
    }

    @Test
    public void testAccountPackage() {
        RequestCtx requestCtx = new RequestCtx(new AccountingRequest(realDictionary, 1, new byte[16]),
                new RadiusEndpoint(new InetSocketAddress(0), "test"));
        radSecHandler.create(session);
        radSecHandler.channelRead0(ctx, requestCtx);
        IRadiusAccountHandlerProvider provider = session
                .getProvider(IRadiusAccountHandlerProvider.class);
        verify(provider).directRead(ctx, requestCtx);
    }


    @Test(expectedExceptions = IllegalStateException.class)
    public void testErrorPackage() {
        RequestCtx requestCtx = new RequestCtx(new RadiusPacket(realDictionary, 1, 1, new byte[16]),
                new RadiusEndpoint(new InetSocketAddress(0), "test"));
        radSecHandler.create(session);
        radSecHandler.channelRead0(ctx, requestCtx);
    }
}
