package ua.zaskarius.keycloak.plugins.radius.configuration;

import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class RadiusSettingFactoryTest extends AbstractRadiusTest {
    private RadiusSettingFactory radiusSettingFactory = new RadiusSettingFactory();

    @Test
    public void testMethods() {
        assertEquals(radiusSettingFactory.getReferenceCategory(), PasswordCredentialModel.TYPE);
        assertEquals(radiusSettingFactory.getRequirementChoices().length, 0);
        assertEquals(radiusSettingFactory.getId(), RadiusSettingFactory.RADIUS_SETTINGS);
        assertEquals(radiusSettingFactory.getDisplayType(), RadiusSettingFactory.RADIUS_SETTINGS);
        assertEquals(radiusSettingFactory.getHelpText(), "");
        assertEquals(radiusSettingFactory.getConfigProperties().size(), 2);

        assertFalse(radiusSettingFactory.isUserSetupAllowed());
        radiusSettingFactory.buildPage(null, null);
        radiusSettingFactory.validate(null);
        radiusSettingFactory.success(null);
        radiusSettingFactory.init(null);
        radiusSettingFactory.postInit(null);
        radiusSettingFactory.close();
        radiusSettingFactory.setRequiredActions(null, null, null);
        assertNull(radiusSettingFactory.create(session));
        assertFalse(radiusSettingFactory.requiresUser());
        assertTrue(radiusSettingFactory.configuredFor(session, realmModel, userModel));


    }
}
