package com.github.vzakharchenko.radius.radius.handlers;

import com.github.vzakharchenko.radius.providers.IRadiusAuthHandlerProvider;
import com.github.vzakharchenko.radius.radius.handlers.session.IAuthRequestInitialization;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoGetter;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import io.netty.channel.ChannelHandlerContext;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.server.RequestCtx;
import org.tinyradius.util.RadiusEndpoint;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.github.vzakharchenko.radius.radius.handlers.AuthHandler.DEFAULT_AUTH_RADIUS_PROVIDER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class AuthHandlerTest extends AbstractRadiusTest {
    private AuthHandler authHandler = new AuthHandler();
    private RequestCtx requestCtx;
    private RadiusEndpoint radiusEndpoint;
    @Mock
    private ChannelHandlerContext channelHandlerContext;

    @Mock
    private IAuthRequestInitialization authRequestInitialization;

    @BeforeMethod
    public void beforeMethods() {
        AccessRequest accessRequest = new AccessRequest(realDictionary,
                0, new byte[16], "test", "p");
        radiusEndpoint = new RadiusEndpoint(InetSocketAddress.createUnresolved("0", 0),
                "testSecret");
        requestCtx = new RequestCtx(accessRequest, radiusEndpoint);

        when(radiusAuthProtocolFactory.create(any(), any())).thenReturn(authProtocol);
        when(authRequestInitialization.init(any(), any(), any(), any())).thenReturn(true);
        reset(channelHandlerContext);
        authHandler.postInit(keycloakSessionFactory);
    }

    @Test
    public void testMethods() {
        assertEquals(authHandler.getId(), DEFAULT_AUTH_RADIUS_PROVIDER);
        IRadiusAuthHandlerProvider iRadiusAuthHandlerProvider = authHandler.create(session);
        assertNotNull(iRadiusAuthHandlerProvider);
        authHandler.close();
        authHandler.init(null);
        authHandler.postInit(null);
        assertNotNull(authHandler.getChannelHandler(session));
        assertEquals(authHandler.acceptedPacketType(), AccessRequest.class);
    }

    @Test
    public void testChannelReadRadius() {
        authHandler.getChannelHandler(session);
        authHandler.setAuthRequestInitialization(authRequestInitialization);
        authHandler.channelReadRadius(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test
    public void testChannelRead0() throws InterruptedException {
        authHandler.getChannelHandler(session);
        authHandler.setAuthRequestInitialization(authRequestInitialization);
        authHandler.channelRead0(channelHandlerContext, requestCtx);
        TimeUnit.SECONDS.sleep(3);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test
    public void testDirectCall() {
        authHandler.getChannelHandler(session);
        authHandler.setAuthRequestInitialization(authRequestInitialization);
        authHandler.directRead(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test
    public void testChannelRead0_Protocol_not_Valid() {
        authHandler.getChannelHandler(session);
        authHandler.setAuthRequestInitialization(authRequestInitialization);
        when(authProtocol.isValid(any())).thenReturn(false);

        authHandler.channelReadRadius(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test
    public void testChannelRead0_exception() {
        authHandler.getChannelHandler(session);
        authHandler.setAuthRequestInitialization(authRequestInitialization);
        when(authProtocol.isValid(any())).thenThrow(new RuntimeException("1"));

        authHandler.channelReadRadius(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test()
    public void testChannelRead0_exception2() {
        authHandler.getChannelHandler(session);
        authHandler.setAuthRequestInitialization(authRequestInitialization);
        when(session.getTransactionManager()).thenThrow(new RuntimeException("1"));

        authHandler.channelReadRadius(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test
    public void testChannelRead0_session1() {
        authHandler.getChannelHandler(session);
        authHandler.setAuthRequestInitialization(authRequestInitialization);
        when(session.getAttribute("RADIUS_INFO",
                IRadiusUserInfoGetter.class))
                .thenReturn(null);

        authHandler.channelReadRadius(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test
    public void testChannelRead0_session2() {
        authHandler.getChannelHandler(session);
        authHandler.setAuthRequestInitialization(authRequestInitialization);
        when(radiusUserInfo.getPasswords()).thenReturn(new ArrayList<>());
        authHandler.channelReadRadius(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test
    public void testChannelRead0_init() {
        authHandler.getChannelHandler(session);
        authHandler.setAuthRequestInitialization(authRequestInitialization);
        when(authRequestInitialization.init(any(), any(), any(), any())).thenReturn(false);

        authHandler.channelReadRadius(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test
    public void testVerifyPassword0() {
        reset(authProtocol);
        when(radiusUserInfo.getPasswords()).thenReturn(Arrays.asList());
        authHandler.verifyPassword0(radiusUserInfoGetter, authProtocol);
        verify(authProtocol).verifyPassword();
    } @Test
    public void testVerifyPassword0_1() {
        reset(authProtocol);
        when(authProtocol.verifyPassword("p1")).thenReturn(true);
        when(radiusUserInfo.getPasswords()).thenReturn(Arrays.asList("p", "p1"));
        authHandler.verifyPassword0(radiusUserInfoGetter, authProtocol);
        verify(authProtocol, never()).verifyPassword();
    }

    @Test
    public void testVerifyPassword0_2() {
        reset(authProtocol);
        when(authProtocol.verifyPassword("p1")).thenReturn(true);
        when(radiusUserInfo.getPasswords()).thenReturn(Arrays.asList("p"));
        authHandler.verifyPassword0(radiusUserInfoGetter, authProtocol);
        verify(authProtocol).verifyPassword();
    }
    @Test
    public void testVerifyPassword0_3() {
        reset(authProtocol);

        authHandler.verifyPassword0(null, authProtocol);
        verify(authProtocol).verifyPassword();
    }


}
