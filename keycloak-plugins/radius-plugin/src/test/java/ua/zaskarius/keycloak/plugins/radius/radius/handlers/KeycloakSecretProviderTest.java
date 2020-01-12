package ua.zaskarius.keycloak.plugins.radius.radius.handlers;

import org.keycloak.models.RealmProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.password.RadiusCredentialModel;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import ua.zaskarius.keycloak.plugins.radius.test.ModelBuilder;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.testng.Assert.*;
import static ua.zaskarius.keycloak.plugins.radius.test.ModelBuilder.SHARED;

public class KeycloakSecretProviderTest extends AbstractRadiusTest {

    private KeycloakSecretProvider keycloakSecretProvider;
    private InetSocketAddress inetSocketAddress;

    @BeforeMethod
    public void beforeMethod() {
        keycloakSecretProvider = new KeycloakSecretProvider(session);
        inetSocketAddress = new InetSocketAddress(ModelBuilder.IP, 0);
    }

    @Test
    public void testIpSharedSecret() {
        String sharedSecret = keycloakSecretProvider.getSharedSecret(inetSocketAddress);
        assertEquals(sharedSecret, "ip_secret");
    }

    @Test
    public void testSharedSecretWrong() {
        String sharedSecret = keycloakSecretProvider
                .getSharedSecret(new InetSocketAddress("111.111.111.111", 0));
        assertEquals(sharedSecret, SHARED);
    }

    @Test
    public void testSharedSecretWrong2() {
        String sharedSecret = keycloakSecretProvider
                .getSharedSecret(InetSocketAddress.createUnresolved("111.111.111.111", 0));
        assertNull(sharedSecret);
    }

    @Test
    public void testgetRadiusPasswordsRealmNull() {
        when(authProtocol.getRealm()).thenReturn(null);
        assertFalse(keycloakSecretProvider
                .init(inetSocketAddress, USER, authProtocol, session));
    }

    @Test
    public void testgetRadiusPasswords() {
        when(userSessionProvider.getUserSessions(realmModel, userModel))
                .thenReturn(Collections.emptyList());
        assertTrue(keycloakSecretProvider
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
        assertTrue(keycloakSecretProvider
                .init(inetSocketAddress, USER, authProtocol, session));

    }

    @Test
    public void testclientEmpty() {
        when(realmModel.getClients()).thenReturn(Collections.emptyList());
        assertFalse(keycloakSecretProvider
                .init(inetSocketAddress, USER, authProtocol, session));

    }

    @Test
    public void testclientWithoutRadius() {
        when(clientModel.getProtocol()).thenReturn("test");
        assertFalse(keycloakSecretProvider
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
        assertTrue(keycloakSecretProvider
                .init(inetSocketAddress, USER, authProtocol, session));
//        assertNotNull(radiusUserInfo);
//        assertEquals(radiusUserInfo.getUserModel(), userModel);
//        assertEquals(radiusUserInfo.getRealmModel(), realmModel);
//        assertEquals(radiusUserInfo.getPasswords().size(), 0);
    }


    @Test
    public void testgetRadiusPasswordsDisabledUser() {
        when(userModel.isEnabled()).thenReturn(false);
        assertFalse(keycloakSecretProvider
                .init(inetSocketAddress, USER, authProtocol, session));

    }

    @Test
    public void testgetRadiusPasswords_Realm_Does_Not_Exists() {
        RealmProvider provider = getProvider(RealmProvider.class);
        when(provider.getRealm(REALM_RADIUS_NAME)).thenReturn(null);
        when(authProtocol.getRealm()).thenReturn(null);
        assertFalse(keycloakSecretProvider
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
        assertFalse(keycloakSecretProvider
                .init(inetSocketAddress, USER, authProtocol, session));

    }

    @Test
    public void testgetafterAuthSUCCESS() {
        when(userSessionProvider.getUserSessions(realmModel, userModel))
                .thenReturn(Collections.emptyList());
        keycloakSecretProvider
                .afterAuth(2, session);
    }

    @Test
    public void testgetafterAuthEROOR() {
        when(userSessionProvider.getUserSessions(realmModel, userModel))
                .thenReturn(Collections.emptyList());
        keycloakSecretProvider
                .afterAuth(4, session);
    }

    @Test
    public void testgetafterAuthRealmERROR() {
        RealmProvider realmProvider = getProvider(RealmProvider.class);
        when(realmProvider.getRealm(REALM_RADIUS_NAME)).thenReturn(null);
        keycloakSecretProvider
                .afterAuth(4, session);
    }


    @Override
    protected List<? extends Object> resetMock() {
        return Arrays.asList();
    }
}
