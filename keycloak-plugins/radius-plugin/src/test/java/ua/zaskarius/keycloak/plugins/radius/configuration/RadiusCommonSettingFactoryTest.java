package ua.zaskarius.keycloak.plugins.radius.configuration;

import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RadiusCommonSettingFactoryTest extends AbstractRadiusTest {
    private RadiusCommonSettingFactory
            radiusCommonSettingFactory =
            new RadiusCommonSettingFactory();

    @Test
    public void testMethod() {
        assertTrue(radiusCommonSettingFactory.isConfigurable());
        assertEquals(radiusCommonSettingFactory.getConfigProperties().size(), 3);
        assertEquals(radiusCommonSettingFactory.getId(), RadiusCommonSettingFactory.RADIUS_PROVIDER_SETTINGS);
        assertEquals(radiusCommonSettingFactory.getReferenceCategory(), PasswordCredentialModel.TYPE);
        assertEquals(radiusCommonSettingFactory.getDisplayType(), "Radius Provider Settings");
        assertEquals(radiusCommonSettingFactory.getHelpText(), "");
        radiusCommonSettingFactory.postInit(keycloakSessionFactory);

    }
}
