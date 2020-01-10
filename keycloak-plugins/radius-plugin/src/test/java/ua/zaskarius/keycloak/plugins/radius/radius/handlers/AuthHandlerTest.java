package ua.zaskarius.keycloak.plugins.radius.radius.handlers;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Promise;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusUserInfo;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.AuthProtocol;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.keycloak.models.KeycloakSession;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;

import java.net.InetSocketAddress;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertNotNull;

public class AuthHandlerTest extends AbstractRadiusTest {

    private AuthHandler authHandler;
    @Mock
    private Dictionary dictionary;
    @Mock
    private IKeycloakSecretProvider secretProvider;
    @Mock
    private Channel channel;

    @Mock
    private EventLoop eventLoop;
    @Mock
    private Promise promise;

    @Mock
    private AuthProtocol authProtocol;

    private RadiusUserInfo radiusUserInfo;


    @BeforeMethod
    public void beforeTests() {
        authHandler = new AuthHandler(session);
        radiusUserInfo = new RadiusUserInfo();
        radiusUserInfo.setPasswords(Arrays.asList("123"));
        radiusUserInfo.setUserModel(userModel);
        reset(channel);
        reset(dictionary);
        reset(secretProvider);
        reset(authProtocol);
        reset(eventLoop);
        reset(promise);
        when(secretProvider
                .init(any(), any(), any(), any(KeycloakSession.class)))
                .thenReturn(true);
        when(radiusAuthProtocolFactory.create(any(), any())).thenReturn(authProtocol);
        when(authProtocol.verifyPassword(anyString())).thenReturn(true);
        when(authProtocol.getRealm()).thenReturn(realmModel);
        when(channel.eventLoop()).thenReturn(eventLoop);
        when(eventLoop.newPromise()).thenReturn(promise);

    }

    @Test
    public void testAuthHandler() {
        AccessRequest request = new AccessRequest(dictionary, 0, new byte[16]);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("111.111.111.111", 0);
        Promise<RadiusPacket> promise = authHandler.handlePacket(channel, request, inetSocketAddress, secretProvider);
        assertNotNull(promise);
        verify(promise).trySuccess(any());
        verify(promise, never()).tryFailure(any());
    }

    @Test
    public void testAuthHandlerIntitFalse() {
        when(secretProvider
                .init(any(), any(), any(), any(KeycloakSession.class)))
                .thenReturn(false);
        AccessRequest request = new AccessRequest(dictionary, 0, new byte[16]);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("111.111.111.111", 0);
        Promise<RadiusPacket> promise = authHandler.handlePacket(channel, request, inetSocketAddress, secretProvider);
        assertNotNull(promise);
        verify(promise).trySuccess(any());
        verify(promise, never()).tryFailure(any());
    }

    @Test
    public void testAuthHandlerProtocolIsNotValid1() {
        when(secretProvider
                .init(any(), any(), any(), any(KeycloakSession.class)))
                .thenReturn(false);
        when(authProtocol.isValid(any())).thenReturn(false);
        AccessRequest request = new AccessRequest(dictionary, 0, new byte[16]);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("111.111.111.111", 0);
        Promise<RadiusPacket> promise = authHandler.handlePacket(channel, request, inetSocketAddress, secretProvider);
        assertNotNull(promise);
        verify(promise).trySuccess(any());
        verify(promise, never()).tryFailure(any());
    }

    @Test
    public void testAuthHandlerProtocolIsNotValid2() {
        when(authProtocol.isValid(any())).thenReturn(false);
        AccessRequest request = new AccessRequest(dictionary, 0, new byte[16]);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("111.111.111.111", 0);
        Promise<RadiusPacket> promise = authHandler.handlePacket(channel, request, inetSocketAddress, secretProvider);
        assertNotNull(promise);
        verify(promise).trySuccess(any());
        verify(promise, never()).tryFailure(any());
    }

    @Test
    public void testAuthHandlerFail() {
        when(radiusAuthProtocolFactory.create(any(), any())).thenReturn(null);
        AccessRequest request = new AccessRequest(dictionary, 0, new byte[16]);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("111.111.111.111", 0);
        Promise<RadiusPacket> promise = authHandler.handlePacket(channel, request, inetSocketAddress, secretProvider);
        assertNotNull(promise);
        verify(promise, never()).trySuccess(any());
        verify(promise).tryFailure(any());
    }


    @Test
    public void testAuthHandlerWithoutPassword() {
        when(authProtocol.verifyPassword(anyString())).thenReturn(false);
        AccessRequest request = new AccessRequest(dictionary, 0, new byte[16]);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("111.111.111.111", 0);
        Promise<RadiusPacket> promise = authHandler.handlePacket(channel, request, inetSocketAddress, secretProvider);
        assertNotNull(promise);
        verify(promise).trySuccess(any());
        verify(promise, never()).tryFailure(any());
    }
}
