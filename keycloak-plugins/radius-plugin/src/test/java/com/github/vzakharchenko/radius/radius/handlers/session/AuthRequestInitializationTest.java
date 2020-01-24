package com.github.vzakharchenko.radius.radius.handlers.session;

import org.keycloak.models.RealmProvider;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.server.SecretProvider;
import com.github.vzakharchenko.radius.password.RadiusCredentialModel;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import com.github.vzakharchenko.radius.test.ModelBuilder;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class AuthRequestInitializationTest extends AbstractRadiusTest {


    private AuthRequestInitialization authRequestInitialization;
    private InetSocketAddress inetSocketAddress;

    @Mock
    SecretProvider secretProvider;

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
        when(userSessionProvider.getUserSessions(realmModel, userModel))
                .thenReturn(Collections.emptyList());
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
        when(userCredentialManager
                .getStoredCredentialsByType(realmModel, userModel,
                        RadiusCredentialModel.TYPE))
                .thenReturn(new ArrayList<>());
        assertTrue(authRequestInitialization
                .init(inetSocketAddress, USER, authProtocol, session));

    }

    @Test
    public void testclientEmpty() {
        when(realmModel.getClients()).thenReturn(Collections.emptyList());
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
        when(userSessionProvider.getUserSessions(realmModel, userModel))
                .thenReturn(Collections.emptyList());
        when(userCredentialManager
                .getStoredCredentialsByType(realmModel, userModel,
                        RadiusCredentialModel.TYPE))
                .thenReturn(new ArrayList<>());
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
    public void testgetRadiusPasswords_Realm_Does_Not_Exists() {
        RealmProvider provider = getProvider(RealmProvider.class);
        when(provider.getRealm(REALM_RADIUS_NAME)).thenReturn(null);
        when(authProtocol.getRealm()).thenReturn(null);
        assertFalse(authRequestInitialization
                .init(inetSocketAddress, USER, authProtocol, session));
    }

    @Test
    public void testgetRadiusPasswords_UserName_Does_Not_Exists() {
        when(userProvider.getUserByUsername(USER, realmModel)).thenReturn(null);
        testgetRadiusPasswords();
    }


    @Test
    public void testgetRadiusPasswords_User_Does_Not_Exists() {
        when(userProvider.getUserByUsername(USER, realmModel)).thenReturn(null);
        when(userProvider.getUserByEmail(USER, realmModel)).thenReturn(null);
        assertFalse(authRequestInitialization
                .init(inetSocketAddress, USER, authProtocol, session));

    }

    @Test
    public void testgetafterAuthSUCCESS() {
        when(userSessionProvider.getUserSessions(realmModel, userModel))
                .thenReturn(Collections.emptyList());
        authRequestInitialization
                .afterAuth(2, session);
    }

    @Test
    public void testgetafterAuthEROOR() {
        when(userSessionProvider.getUserSessions(realmModel, userModel))
                .thenReturn(Collections.emptyList());
        authRequestInitialization
                .afterAuth(4, session);
    }

    @Test
    public void testgetafterAuthRealmERROR() {
        RealmProvider realmProvider = getProvider(RealmProvider.class);
        when(realmProvider.getRealm(REALM_RADIUS_NAME)).thenReturn(null);
        authRequestInitialization
                .afterAuth(4, session);
    }


    @Override
    protected List<? extends Object> resetMock() {
        return Arrays.asList();
    }
}
