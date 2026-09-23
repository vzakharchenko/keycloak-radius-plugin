package com.github.vzakharchenko.radius.radius.handlers.session;

import com.github.vzakharchenko.radius.password.RadiusCredentialModel;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import com.github.vzakharchenko.radius.test.ModelBuilder;
import jakarta.enterprise.context.ContextNotActiveException;
import org.keycloak.credential.CredentialModel;
import org.keycloak.events.Event;
import org.keycloak.events.EventStoreProvider;
import org.keycloak.models.RealmProvider;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.server.SecretProvider;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.tinyradius.packet.PacketType.ACCESS_ACCEPT;
import static org.tinyradius.packet.PacketType.ACCESS_REJECT;

public class AuthRequestInitializationTest extends AbstractRadiusTest {


    @Mock
    SecretProvider secretProvider;
    private AuthRequestInitialization authRequestInitialization;
    private InetSocketAddress inetSocketAddress;

    @BeforeMethod
    public void beforeMethod() {
        reset(secretProvider);
        when(secretProvider.getSharedSecret(any())).thenReturn("test");
        authRequestInitialization = new AuthRequestInitialization(secretProvider);
        inetSocketAddress = new InetSocketAddress(ModelBuilder.IP, 0);
    }


    @Test
    public void testgetRadiusPasswordsRealmNull() {
        when(authProtocol.getRealm()).thenReturn(null);
        assertFalse(authRequestInitialization
                .init(inetSocketAddress, USER, authProtocol, session));
    }

    @Test
    public void testgetRadiusPasswords() {
        when(userSessionProvider.getUserSessionsStream(realmModel, userModel))
                .thenAnswer(i -> Stream.empty());
        assertTrue(authRequestInitialization
                .init(inetSocketAddress, USER, authProtocol, session));
//        assertNotNull(radiusUserInfo);
//        assertEquals(radiusUserInfo.getUserModel(), userModel);
//        assertEquals(radiusUserInfo.getRealmModel(), realmModel);
//        assertEquals(radiusUserInfo.getPasswords().size(), 1);
//        assertEquals(radiusUserInfo.getPasswords().get(0), "secret");
    }

    @Test
    public void testgetRadiusSessionPasswords() {
        when(subjectCredentialManager
                .getStoredCredentialsByTypeStream(
                        RadiusCredentialModel.TYPE))
                .thenReturn(new ArrayList<CredentialModel>().stream());
        assertTrue(authRequestInitialization
                .init(inetSocketAddress, USER, authProtocol, session));

    }

    @Test
    public void testclientEmpty() {
        when(realmModel.getClientsStream()).thenAnswer(i -> Stream.empty());
        assertFalse(authRequestInitialization
                .init(inetSocketAddress, USER, authProtocol, session));

    }

    @Test
    public void testclientWithoutRadius() {
        when(clientModel.getProtocol()).thenReturn("test");
        assertFalse(authRequestInitialization
                .init(inetSocketAddress, USER, authProtocol, session));

    }

    @Test
    public void testgetRadiusPasswordsWithoutPassword() {
        when(userSessionProvider.getUserSessionsStream(realmModel, userModel))
                .thenAnswer(i -> Stream.empty());
        when(subjectCredentialManager
                .getStoredCredentialsByTypeStream(
                        RadiusCredentialModel.TYPE))
                .thenReturn(new ArrayList<CredentialModel>().stream());
        assertTrue(authRequestInitialization
                .init(inetSocketAddress, USER, authProtocol, session));
//        assertNotNull(radiusUserInfo);
//        assertEquals(radiusUserInfo.getUserModel(), userModel);
//        assertEquals(radiusUserInfo.getRealmModel(), realmModel);
//        assertEquals(radiusUserInfo.getPasswords().size(), 0);
    }


    @Test
    public void testgetRadiusPasswordsDisabledUser() {
        when(userModel.isEnabled()).thenReturn(false);
        assertFalse(authRequestInitialization
                .init(inetSocketAddress, USER, authProtocol, session));

    }

    @Test
    public void testgetRadiusPasswordsRealmDoesNotExists() {
        RealmProvider provider = getProvider(RealmProvider.class);
        when(provider.getRealm(REALM_RADIUS_ID)).thenReturn(null);
        when(authProtocol.getRealm()).thenReturn(null);
        assertFalse(authRequestInitialization
                .init(inetSocketAddress, USER, authProtocol, session));
    }

    @Test
    public void testgetRadiusPasswordsUserNameDoesNotExists() {
        when(userProvider.getUserByUsername(realmModel, USER)).thenReturn(null);
        testgetRadiusPasswords();
    }


    @Test
    public void testgetRadiusPasswordsUserDoesNotExists() {
        when(userProvider.getUserByUsername(realmModel, USER)).thenReturn(null);
        when(userProvider.getUserByEmail(realmModel, USER)).thenReturn(null);
        assertFalse(authRequestInitialization
                .init(inetSocketAddress, USER, authProtocol, session));

    }

    @Test
    public void testgetafterAuthSUCCESS() {
        when(userSessionProvider.getUserSessionsStream(realmModel, userModel))
                .thenAnswer(i -> Stream.empty());
        authRequestInitialization
                .afterAuth(2, session);
    }

    @Test
    public void testgetafterAuthReject() {
        when(userSessionProvider.getUserSessionsStream(realmModel, userModel))
                .thenAnswer(i -> Stream.empty());
        authRequestInitialization
                .afterAuth(3, session);
    }

    @Test
    public void testgetafterAuthEROOR() {
        when(userSessionProvider.getUserSessionsStream(realmModel, userModel))
                .thenAnswer(i -> Stream.empty());
        authRequestInitialization
                .afterAuth(4, session);
    }

    @Test
    public void testgetafterAuthRealmERROR() {
        RealmProvider realmProvider = getProvider(RealmProvider.class);
        when(realmProvider.getRealm(REALM_RADIUS_ID)).thenReturn(null);
        authRequestInitialization
                .afterAuth(4, session);
    }

    @Test
    public void testInitWithContextNotActiveExceptionAndUserDoesNotExist() {
        EventStoreProvider eventStoreProvider = getProvider(EventStoreProvider.class);
        when(userProvider.getUserByUsername(realmModel, USER)).thenReturn(null);
        when(userProvider.getUserByEmail(realmModel, USER)).thenReturn(null);
        when(realmModel.isEventsEnabled()).thenReturn(true);
        doThrow(new ContextNotActiveException("no HTTP request context")).when(session).close();
        assertFalse(authRequestInitialization.init(inetSocketAddress, USER, authProtocol, session));
        verify(eventStoreProvider).onEvent(any(Event.class));
    }

    @Test
    public void testAfterAuthAcceptWithContextNotActiveException() {
        EventStoreProvider eventStoreProvider = getProvider(EventStoreProvider.class);
        when(realmModel.isEventsEnabled()).thenReturn(true);
        doThrow(new ContextNotActiveException("no HTTP request context")).when(session).close();
        authRequestInitialization.afterAuth(ACCESS_ACCEPT, session);
        verify(eventStoreProvider).onEvent(any(Event.class));
    }

    @Test
    public void testAfterAuthRejectWithContextNotActiveException() {
        EventStoreProvider eventStoreProvider = getProvider(EventStoreProvider.class);
        when(realmModel.isEventsEnabled()).thenReturn(true);
        doThrow(new ContextNotActiveException("no HTTP request context")).when(session).close();
        authRequestInitialization.afterAuth(ACCESS_REJECT, session);
        verify(eventStoreProvider).onEvent(any(Event.class));
    }

    @Override
    protected List<? extends Object> resetMock() {
        return Arrays.asList();
    }
}
