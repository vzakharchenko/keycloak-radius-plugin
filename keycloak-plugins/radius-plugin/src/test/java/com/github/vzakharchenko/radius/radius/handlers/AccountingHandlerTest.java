package com.github.vzakharchenko.radius.radius.handlers;

import com.github.vzakharchenko.radius.providers.IRadiusAccountHandlerProvider;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import io.netty.channel.ChannelHandlerContext;
import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.server.RequestCtx;
import org.tinyradius.util.RadiusEndpoint;

import java.net.InetSocketAddress;

import static com.github.vzakharchenko.radius.radius.handlers.AccountingHandler.ACCT_AUTHENTIC;
import static com.github.vzakharchenko.radius.radius.handlers.session.AccountingSessionManager.ACCT_STATUS_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class AccountingHandlerTest extends AbstractRadiusTest {
    private AccountingHandler accountingHandler = new AccountingHandler();
    private RequestCtx requestCtx;
    private RadiusEndpoint radiusEndpoint;
    @Mock
    private ChannelHandlerContext channelHandlerContext;

    private AccountingRequest accountingRequest;

    @BeforeMethod
    public void beforeMethods() {
        accountingRequest = new AccountingRequest(realDictionary,
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
        Assert.assertEquals(accountingHandler.getId(), AccountingHandler.DEFAULT_ACCOUNT_RADIUS_PROVIDER);
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
    public void testChannelRead0NotValid() {
        accountingRequest = new AccountingRequest(realDictionary,
                0, new byte[16], "test", 2);
        accountingRequest.addAttribute("realm-radius", realmModel.getName());
        requestCtx = new RequestCtx(accountingRequest, radiusEndpoint);
        accountingHandler.channelRead0(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }
    @Test
    public void testChannelRead0Local() {
        accountingRequest.addAttribute(ACCT_AUTHENTIC,"02");
        accountingHandler.channelRead0(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test()
    public void testChannelRead0_exception2() {
        accountingHandler.getChannelHandler(session);
        when(session.getTransactionManager()).thenThrow(new RuntimeException("1"));

        accountingHandler.channelRead0(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test
    public void testDirectCall() {
        accountingHandler.directRead(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }
}
