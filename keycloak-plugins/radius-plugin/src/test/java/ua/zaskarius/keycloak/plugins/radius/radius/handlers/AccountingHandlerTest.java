package ua.zaskarius.keycloak.plugins.radius.radius.handlers;

import io.netty.channel.ChannelHandlerContext;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.server.RequestCtx;
import org.tinyradius.util.RadiusEndpoint;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAccountHandlerProvider;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.net.InetSocketAddress;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ua.zaskarius.keycloak.plugins.radius.radius.handlers.AccountingHandler.DEFAULT_ACCOUNT_RADIUS_PROVIDER;

public class AccountingHandlerTest extends AbstractRadiusTest {
    private AccountingHandler accountingHandler = new AccountingHandler();
    private RequestCtx requestCtx;
    private RadiusEndpoint radiusEndpoint;
    @Mock
    private ChannelHandlerContext channelHandlerContext;

    @BeforeMethod
    public void beforeMethods() {
        AccountingRequest accountingRequest = new AccountingRequest(realDictionary,
                0, new byte[16], "test", 1);
        accountingRequest.addAttribute("realm-radius", realmModel.getName());
        accountingHandler.postInit(keycloakSessionFactory);
        radiusEndpoint = new RadiusEndpoint(InetSocketAddress.createUnresolved("0", 0),
                "testSecret");
        requestCtx = new RequestCtx(accountingRequest, radiusEndpoint);
        reset(channelHandlerContext);
    }

    @Test
    public void testMethods() {
        assertEquals(accountingHandler.getId(), DEFAULT_ACCOUNT_RADIUS_PROVIDER);
        IRadiusAccountHandlerProvider accountHandlerProvider = accountingHandler.create(session);
        assertNotNull(accountHandlerProvider);
        accountingHandler.close();
        accountingHandler.init(null);
        accountingHandler.postInit(null);
        assertEquals(accountingHandler.getChannelHandler(session), accountingHandler);
        assertEquals(accountingHandler.acceptedPacketType(), AccountingRequest.class);
    }

    @Test
    public void testChannelRead0() {
        accountingHandler.channelRead0(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test
    public void testDirectCall() {
        accountingHandler.directRead(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }
}
