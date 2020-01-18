package ua.zaskarius.keycloak.plugins.radius.radius.handlers;

import io.netty.channel.ChannelHandlerContext;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.server.RequestCtx;
import org.tinyradius.util.RadiusEndpoint;
import ua.zaskarius.keycloak.plugins.radius.radius.holder.IRadiusUserInfoGetter;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAuthHandlerProvider;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.session.IAuthRequestInitialization;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ua.zaskarius.keycloak.plugins.radius.radius.handlers.AuthHandler.DEFAULT_AUTH_RADIUS_PROVIDER;

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
    public void testChannelRead0() {
        authHandler.getChannelHandler(session);
        authHandler.setAuthRequestInitialization(authRequestInitialization);
        authHandler.channelRead0(channelHandlerContext, requestCtx);
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

        authHandler.channelRead0(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test
    public void testChannelRead0_exception() {
        authHandler.getChannelHandler(session);
        authHandler.setAuthRequestInitialization(authRequestInitialization);
        when(authProtocol.isValid(any())).thenThrow(new RuntimeException("1"));

        authHandler.channelRead0(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testChannelRead0_exception2() {
        authHandler.getChannelHandler(session);
        authHandler.setAuthRequestInitialization(authRequestInitialization);
        when(session.getTransactionManager()).thenThrow(new RuntimeException("1"));

        authHandler.channelRead0(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test
    public void testChannelRead0_session1() {
        authHandler.getChannelHandler(session);
        authHandler.setAuthRequestInitialization(authRequestInitialization);
        when(session.getAttribute("RADIUS_INFO",
                IRadiusUserInfoGetter.class))
                .thenReturn(null);

        authHandler.channelRead0(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test
    public void testChannelRead0_session2() {
        authHandler.getChannelHandler(session);
        authHandler.setAuthRequestInitialization(authRequestInitialization);
        when(radiusUserInfo.getPasswords()).thenReturn(new ArrayList<>());
        authHandler.channelRead0(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }

    @Test
    public void testChannelRead0_init() {
        authHandler.getChannelHandler(session);
        authHandler.setAuthRequestInitialization(authRequestInitialization);
        when(authRequestInitialization.init(any(), any(), any(), any())).thenReturn(false);

        authHandler.channelRead0(channelHandlerContext, requestCtx);
        verify(channelHandlerContext).writeAndFlush(any());
    }
}
