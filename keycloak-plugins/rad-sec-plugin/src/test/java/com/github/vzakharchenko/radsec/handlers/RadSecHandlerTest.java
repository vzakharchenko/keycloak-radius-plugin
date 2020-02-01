package com.github.vzakharchenko.radsec.handlers;

import com.github.vzakharchenko.radius.providers.IRadiusAccountHandlerProvider;
import com.github.vzakharchenko.radius.providers.IRadiusAuthHandlerProvider;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import io.netty.channel.ChannelHandlerContext;
import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.server.RequestCtx;
import org.tinyradius.util.RadiusEndpoint;

import java.net.InetSocketAddress;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

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
        Assert.assertEquals(radSecHandler.create(session), radSecHandler);
        assertEquals(radSecHandler.acceptedPacketType(), RadiusPacket.class);
        assertEquals(radSecHandler.getChannelHandler(session), radSecHandler);
        radSecHandler.close();
        radSecHandler.init(null);
        radSecHandler.postInit(null);
        Assert.assertEquals(radSecHandler.getId(), RadSecHandler.DEFAULT_RADSEC_RADIUS_PROVIDER);
    }


    @Test
    public void testAccessPackage() {
        RequestCtx requestCtx = new RequestCtx(new AccessRequest(realDictionary, 1, new byte[16]),
                new RadiusEndpoint(new InetSocketAddress(0), "test"));
        radSecHandler.create(session);
        radSecHandler.channelReadRadius(ctx, requestCtx);
        IRadiusAuthHandlerProvider provider = session
                .getProvider(IRadiusAuthHandlerProvider.class);
        verify(provider).directRead(ctx, requestCtx);
    }

    @Test
    public void testAccountPackage() {
        RequestCtx requestCtx = new RequestCtx(new AccountingRequest(realDictionary, 1, new byte[16]),
                new RadiusEndpoint(new InetSocketAddress(0), "test"));
        radSecHandler.create(session);
        radSecHandler.channelReadRadius(ctx, requestCtx);
        IRadiusAccountHandlerProvider provider = session
                .getProvider(IRadiusAccountHandlerProvider.class);
        verify(provider).directRead(ctx, requestCtx);
    }


    @Test(expectedExceptions = IllegalStateException.class)
    public void testErrorPackage() {
        RequestCtx requestCtx = new RequestCtx(new RadiusPacket(realDictionary, 1, 1, new byte[16]),
                new RadiusEndpoint(new InetSocketAddress(0), "test"));
        radSecHandler.create(session);
        radSecHandler.channelReadRadius(ctx, requestCtx);
    }
}
