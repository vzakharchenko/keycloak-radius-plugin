package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import static org.testng.Assert.*;

public class DBRadiusConfigurationTest extends AbstractRadiusTest {
    private DBRadiusConfiguration dbRadiusConfiguration = new DBRadiusConfiguration();

    @Test
    public void testMethods(){
        assertNull(dbRadiusConfiguration.getCommonSettings(realmModel));
        assertNull(dbRadiusConfiguration.getRadiusSettings(realmModel));
        dbRadiusConfiguration.init(realmModel);
        assertTrue(dbRadiusConfiguration.isUsedRadius(realmModel));
        assertTrue(dbRadiusConfiguration.isUsedRadius(session));
        RadiusServerSettings radiusSettings = dbRadiusConfiguration
                .getRadiusSettings(session);
        assertNotNull(radiusSettings);
    }
}
