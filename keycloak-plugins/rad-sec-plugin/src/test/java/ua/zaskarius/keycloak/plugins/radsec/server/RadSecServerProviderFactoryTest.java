package ua.zaskarius.keycloak.plugins.radsec.server;

import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.models.RadSecSettings;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static ua.zaskarius.keycloak.plugins.radsec.server.RadSecServerProviderFactory.RADSEC_PROVIDER;

public class RadSecServerProviderFactoryTest extends AbstractRadiusTest {
    public RadSecServerProviderFactory radSecServerProviderFactory =
            new RadSecServerProviderFactory();

    @Test
    public void testMethods() {
        radSecServerProviderFactory.postInit(session, null);
        assertEquals(radSecServerProviderFactory.getId(), RADSEC_PROVIDER);
    }

    @Test
    public void testCreateInstance() {
        RadiusServerSettings settings = new RadiusServerSettings();
        when(configuration.getRadiusSettings()).thenReturn(settings);
        RadSecSettings radSecSettings = new RadSecSettings();
        radSecSettings.setUseRadSec(false);
        settings.setRadSecSettings(radSecSettings);
        radSecServerProviderFactory.createInstance(session);
    }

    @Test
    public void testCreateInstanceStpped() {
        radSecServerProviderFactory.createInstance(session);
    }


}
