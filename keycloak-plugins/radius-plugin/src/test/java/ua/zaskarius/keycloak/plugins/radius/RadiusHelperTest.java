package ua.zaskarius.keycloak.plugins.radius;

import ua.zaskarius.keycloak.plugins.radius.password.RadiusCredentialModel;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusConnectionProvider;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.radius.provider.RadiusRadiusProvider;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class RadiusHelperTest extends AbstractRadiusTest {


    @Test
    public void testHasPasswordReadPermission() {
        assertTrue(RadiusHelper.hasPasswordReadPermission(realmModel, userModel));
    }

    @Test
    public void testHasPasswordReadPermission_Role_Does_not_exists() {
        when(realmModel.getRole(RadiusRadiusProvider.READ_MIKROTIK_PASSWORD)).thenReturn(null);
        assertFalse(RadiusHelper.hasPasswordReadPermission(realmModel, userModel));
    }

    @Test
    public void testHasPasswordReadPermission_UserDisabled() {
        when(userModel.isEnabled()).thenReturn(false);
        assertFalse(RadiusHelper.hasPasswordReadPermission(realmModel, userModel));
    }

    @Test
    public void testHasPasswordReadPermission_Does_not_have_role() {
        when(userModel.hasRole(radiusRole)).thenReturn(false);
        assertFalse(RadiusHelper.hasPasswordReadPermission(realmModel, userModel));
    }

    @Test
    public void testPassword() {
        String password = RadiusHelper.getPassword(session, realmModel, userModel);
        assertEquals(password, "secret");
    }

    @Test(expectedExceptions = IllegalStateException.class,
            expectedExceptionsMessageRegExp = "USER does not have radius password")
    public void testPasswordEmptyCredential() {
        when(userCredentialManager
                .getStoredCredentialsByType(realmModel, userModel,
                        RadiusCredentialModel.TYPE))
                .thenReturn(new ArrayList<>());
        assertNull(RadiusHelper.getPassword(session, realmModel, userModel));
    }

    @Test(expectedExceptions = IllegalStateException.class,
            expectedExceptionsMessageRegExp = "USER does not have role "
                    + RadiusRadiusProvider.READ_MIKROTIK_PASSWORD)
    public void testPasswordWithoutPermission() {
        when(realmModel.getRole(RadiusRadiusProvider.READ_MIKROTIK_PASSWORD)).thenReturn(null);
        RadiusHelper.getPassword(session, realmModel, userModel);
    }


    @Test
    public void testCurrentPassword() {
        String password = RadiusHelper.getCurrentPassword(session, realmModel, userModel);
        assertEquals(password, "secret");
    }

    @Test
    public void testCurrentPasswordEmptyCredential() {
        when(userCredentialManager
                .getStoredCredentialsByType(realmModel, userModel,
                        RadiusCredentialModel.TYPE))
                .thenReturn(new ArrayList<>());
        assertNull(RadiusHelper.getCurrentPassword(session, realmModel, userModel));
    }

    @Test
    public void testCurrentPasswordWithoutPermission() {
        when(realmModel.getRole(RadiusRadiusProvider.READ_MIKROTIK_PASSWORD)).thenReturn(null);
        assertNull(RadiusHelper.getCurrentPassword(session, realmModel, userModel));
    }

    @Test
    public void testGetProvider() {
        IRadiusConnectionProvider provider = RadiusHelper
                .getProvider(session, realmModel);
        assertNotNull(provider);
    }

    @Test
    public void testIsUseRadius() {
        assertTrue(RadiusHelper.isUseRadius(realmModel));
    }

    @Test
    public void testGeneratePassword() {
        assertNotNull(RadiusHelper.generatePassword());
    }

    @Override
    protected List<? extends Object> resetMock() {
        return null;
    }
}
