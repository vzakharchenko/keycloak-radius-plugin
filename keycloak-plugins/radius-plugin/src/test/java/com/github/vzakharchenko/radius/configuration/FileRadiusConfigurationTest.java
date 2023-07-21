package com.github.vzakharchenko.radius.configuration;

import com.github.vzakharchenko.radius.models.RadiusServerSettings;
import com.github.vzakharchenko.radius.models.file.CoASettingsModel;
import com.github.vzakharchenko.radius.models.file.RadSecSettingsModel;
import com.github.vzakharchenko.radius.models.file.RadiusAccessModel;
import com.github.vzakharchenko.radius.models.file.RadiusConfigModel;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.apache.commons.io.FileUtils;
import org.keycloak.util.JsonSerialization;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.testng.Assert.*;
import static uk.org.webcompere.systemstubs.SystemStubs.withEnvironmentVariable;

public class FileRadiusConfigurationTest extends AbstractRadiusTest {
    private final FileRadiusConfiguration radiusConfiguration = new FileRadiusConfiguration();
    private final File config = new File("config", "radius.config");

    private RadiusConfigModel radiusConfigModel;

    @BeforeMethod
    public void beforeMethod() throws IOException {
        radiusConfigModel = new RadiusConfigModel();
        radiusConfigModel.setAuthPort(1813);
        radiusConfigModel.setSharedSecret("GlobalShared");
        radiusConfigModel.setNumberThreads(19);
        radiusConfigModel.setUseUdpRadius(true);
        radiusConfigModel.setOtp(true);
        RadiusAccessModel radiusAccessModel = new RadiusAccessModel();
        radiusAccessModel.setIp("ip");
        radiusAccessModel.setSharedSecret("ip");
        radiusConfigModel.setRadiusIpAccess(Arrays.asList(radiusAccessModel));
        radiusConfiguration.setRadiusSettings(null);
        CoASettingsModel coASettingsModel = new CoASettingsModel();
        coASettingsModel.setPort(1000);
        coASettingsModel.setUseCoA(true);
        radiusConfigModel.setCoa(coASettingsModel);
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
    public void testMethodsEnvs() throws Exception {
        withEnvironmentVariable(FileRadiusConfiguration.FILE_VARIABLE, ".")
                .execute(() -> {
                    assertEquals(System.getenv(FileRadiusConfiguration.FILE_VARIABLE), ".");
                    RadiusServerSettings radiusSettings = radiusConfiguration.getRadiusSettings();
                    assertNotNull(radiusSettings);
                    assertEquals(radiusSettings.getAccountPort(), 1813);
                });

    }

    @Test
    public void testRadiusEnvs() throws Exception {
        withEnvironmentVariable(FileRadiusConfiguration.FILE_CONFIG_VARIABLE, "./config")
                .execute(() -> {
                    assertEquals(System.getenv(FileRadiusConfiguration.FILE_CONFIG_VARIABLE), "./config");
                    RadiusServerSettings radiusSettings = radiusConfiguration.getRadiusSettings();
                    assertNotNull(radiusSettings);
                    assertEquals(radiusSettings.getAccountPort(), 1813);
                });

    }

    @Test
    public void testMethods2() {
        RadiusServerSettings radiusSettings = radiusConfiguration.getRadiusSettings();
        radiusSettings = radiusConfiguration.getRadiusSettings();
        assertNotNull(radiusSettings);
        assertEquals(radiusSettings.getAccountPort(), 1813);
    }

    @Test
    public void testRadSecConfigurationDefaultFalse() throws IOException {
        radiusConfigModel.setRadsec(new RadSecSettingsModel());
        FileUtils.write(config,
                JsonSerialization.writeValueAsPrettyString(radiusConfigModel));
        RadiusServerSettings radiusSettings = radiusConfiguration.getRadiusSettings();
        radiusSettings = radiusConfiguration.getRadiusSettings();
        assertNotNull(radiusSettings);
        assertFalse(radiusSettings.getRadSecSettings().isUseRadSec());
    }

    @Test
    public void testRadSecConfigurationTrue() throws IOException {
        RadSecSettingsModel radsec = new RadSecSettingsModel();
        radsec.setUseRadSec(true);
        radiusConfigModel.setRadsec(radsec);
        FileUtils.write(config,
                JsonSerialization.writeValueAsPrettyString(radiusConfigModel));
        RadiusServerSettings radiusSettings = radiusConfiguration.getRadiusSettings();
        radiusSettings = radiusConfiguration.getRadiusSettings();
        assertNotNull(radiusSettings);
        assertTrue(radiusSettings.getRadSecSettings().isUseRadSec());
    }

    @Test
    public void testRadSecConfigurationNull() throws IOException {
        radiusConfigModel.setRadsec(null);
        FileUtils.write(config,
                JsonSerialization.writeValueAsPrettyString(radiusConfigModel));
        RadiusServerSettings radiusSettings = radiusConfiguration.getRadiusSettings();
        radiusSettings = radiusConfiguration.getRadiusSettings();
        assertNotNull(radiusSettings);
        assertFalse(radiusSettings.getRadSecSettings().isUseRadSec());
    }

    @Test
    public void testCoaConfigurationNull() throws IOException {
        radiusConfigModel.setCoa(null);
        FileUtils.write(config,
                JsonSerialization.writeValueAsPrettyString(radiusConfigModel));
        RadiusServerSettings radiusSettings = radiusConfiguration.getRadiusSettings();
        radiusSettings = radiusConfiguration.getRadiusSettings();
        assertNotNull(radiusSettings);
        assertFalse(radiusSettings.getCoASettings().isUseCoAPackage());
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
