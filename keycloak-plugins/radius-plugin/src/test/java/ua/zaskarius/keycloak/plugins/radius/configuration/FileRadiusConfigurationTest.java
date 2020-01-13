package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.apache.commons.io.FileUtils;
import org.keycloak.util.JsonSerialization;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusAccessModel;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusConfigModel;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class FileRadiusConfigurationTest extends AbstractRadiusTest {
    private FileRadiusConfiguration radiusConfiguration = new FileRadiusConfiguration();
    private File config = new File("config", "radius.config");

    @BeforeMethod
    public void beforeMethod() throws IOException {
        RadiusConfigModel radiusConfigModel = new RadiusConfigModel();
        radiusConfigModel.setAuthPort(1813);
        radiusConfigModel.setProvider("test");
        radiusConfigModel.setSharedSecret("GlobalShared");
        RadiusAccessModel radiusAccessModel = new RadiusAccessModel();
        radiusAccessModel.setIp("ip");
        radiusAccessModel.setSharedSecret("ip");
        radiusConfigModel.setRadiusIpAccess(Arrays.asList(radiusAccessModel));
        radiusConfiguration.setRadiusSettings(null);

        FileUtils.write(config,
                JsonSerialization.writeValueAsPrettyString(radiusConfigModel));
    }

    @AfterMethod
    public void afterNethods() {
        FileUtils.deleteQuietly(config);
    }

    @Test
    public void testMethods() {
        RadiusServerSettings radiusSettings = radiusConfiguration.getRadiusSettings();
        assertNotNull(radiusSettings);
        assertEquals(radiusSettings.getAccountPort(), 1813);
    }

    @Test
    public void testMethods2() {
        RadiusServerSettings radiusSettings = radiusConfiguration.getRadiusSettings();
        radiusSettings = radiusConfiguration.getRadiusSettings();
        assertNotNull(radiusSettings);
        assertEquals(radiusSettings.getAccountPort(), 1813);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMethodsDoesNotexists() {
        FileUtils.deleteQuietly(config);
        RadiusServerSettings radiusSettings = radiusConfiguration.getRadiusSettings();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMethodsWrongStrucure() throws IOException {
        FileUtils.deleteQuietly(config);
        FileUtils.write(config, "test");
        RadiusServerSettings radiusSettings = radiusConfiguration.getRadiusSettings();
    }
}
