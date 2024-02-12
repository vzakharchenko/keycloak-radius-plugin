package com.github.vzakharchenko.radius.configuration;

import com.github.vzakharchenko.radius.models.RadiusServerSettings;
import com.github.vzakharchenko.radius.models.file.CoASettingsModel;
import com.github.vzakharchenko.radius.models.file.RadSecSettingsModel;
import com.github.vzakharchenko.radius.models.file.RadiusAccessModel;
import com.github.vzakharchenko.radius.models.file.RadiusConfigModel;
import com.github.vzakharchenko.radius.radius.handlers.protocols.ProtocolType;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.apache.commons.io.FileUtils;
import org.keycloak.util.JsonSerialization;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

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
        radiusConfigModel.setOtpWithoutPassword(Set.of("CHAP", "MSCHAPV2", "PAP"));
        RadiusAccessModel radiusAccessModel = new RadiusAccessModel();
        radiusAccessModel.setIp("ip");
        radiusAccessModel.setSharedSecret("ip");
        radiusConfigModel.setRadiusIpAccess(List.of(radiusAccessModel));
        CoASettingsModel coASettingsModel = new CoASettingsModel();
        coASettingsModel.setPort(1000);
        coASettingsModel.setUseCoA(true);
        radiusConfigModel.setCoa(coASettingsModel);
        writeModelAndRestSettings();
    }

    @AfterMethod
    public void afterMethods() {
        FileUtils.deleteQuietly(config);
    }



    private void writeModelAndRestSettings() throws IOException {
        FileUtils.write(config,
                JsonSerialization.writeValueAsPrettyString(radiusConfigModel),
                Charset.defaultCharset());
        radiusConfiguration.setRadiusSettings(null);
    }
    private RadiusServerSettings writeModelAndGetSettings() throws IOException {
        writeModelAndRestSettings();
        return radiusConfiguration.getRadiusSettings();
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
                    assertEquals(System.getenv(FileRadiusConfiguration.FILE_CONFIG_VARIABLE),
                            "./config");
                    RadiusServerSettings radiusSettings = radiusConfiguration.getRadiusSettings();
                    assertNotNull(radiusSettings);
                    assertEquals(radiusSettings.getAccountPort(), 1813);
                });

    }

    @Test
    public void testMethods2() {
        RadiusServerSettings radiusSettings = radiusConfiguration.getRadiusSettings();
        assertNotNull(radiusSettings);
        assertEquals(radiusSettings.getAccountPort(), 1813);
    }

    @Test
    public void testRadSecConfigurationDefaultFalse() throws IOException {
        radiusConfigModel.setRadsec(new RadSecSettingsModel());
        RadiusServerSettings radiusSettings = writeModelAndGetSettings();
        assertNotNull(radiusSettings);
        assertFalse(radiusSettings.getRadSecSettings().isUseRadSec());
    }

    @Test
    public void testRadSecConfigurationTrue() throws IOException {
        RadSecSettingsModel radsec = new RadSecSettingsModel();
        radsec.setUseRadSec(true);
        radiusConfigModel.setRadsec(radsec);
        RadiusServerSettings radiusSettings = writeModelAndGetSettings();
        assertNotNull(radiusSettings);
        assertTrue(radiusSettings.getRadSecSettings().isUseRadSec());
    }

    @Test
    public void testRadSecConfigurationNull() throws IOException {
        radiusConfigModel.setRadsec(null);
        RadiusServerSettings radiusSettings = writeModelAndGetSettings();
        assertNotNull(radiusSettings);
        assertFalse(radiusSettings.getRadSecSettings().isUseRadSec());
    }

    @Test
    public void testCoaConfigurationNull() throws IOException {
        radiusConfigModel.setCoa(null);
        RadiusServerSettings radiusSettings = writeModelAndGetSettings();
        assertNotNull(radiusSettings);
        assertFalse(radiusSettings.getCoASettings().isUseCoAPackage());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMethodsDoesNotExists() {
        FileUtils.deleteQuietly(config);
        radiusConfiguration.getRadiusSettings();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMethodsWrongStructure() throws IOException {
        FileUtils.deleteQuietly(config);
        FileUtils.write(config, "test", Charset.defaultCharset());
        radiusConfiguration.getRadiusSettings();
    }

    @Test
    public void testOtpWithoutPasswordOnAll() {
        RadiusServerSettings radiusSettings = radiusConfiguration.getRadiusSettings();
        assertNotNull(radiusSettings);
        assertTrue(radiusSettings.isOtpWithoutPassword(ProtocolType.CHAP));
        assertTrue(radiusSettings.isOtpWithoutPassword(ProtocolType.MSCHAPV2));
        assertTrue(radiusSettings.isOtpWithoutPassword(ProtocolType.PAP));
    }

    @Test
    public void testOtpWithoutPasswordOnChap() throws IOException {
        radiusConfigModel.setOtpWithoutPassword(Set.of("CHAP"));
        RadiusServerSettings radiusSettings = writeModelAndGetSettings();
        assertNotNull(radiusSettings);
        assertTrue(radiusSettings.isOtpWithoutPassword(ProtocolType.CHAP));
        assertFalse(radiusSettings.isOtpWithoutPassword(ProtocolType.MSCHAPV2));
        assertFalse(radiusSettings.isOtpWithoutPassword(ProtocolType.PAP));
    }

    @Test
    public void testOtpWithoutPasswordOnMsChapV2() throws IOException {
        radiusConfigModel.setOtpWithoutPassword(Set.of("MSCHAPV2"));
        RadiusServerSettings radiusSettings = writeModelAndGetSettings();
        assertNotNull(radiusSettings);
        assertFalse(radiusSettings.isOtpWithoutPassword(ProtocolType.CHAP));
        assertTrue(radiusSettings.isOtpWithoutPassword(ProtocolType.MSCHAPV2));
        assertFalse(radiusSettings.isOtpWithoutPassword(ProtocolType.PAP));
    }

    @Test
    public void testOtpWithoutPasswordOnPAP() throws IOException {
        radiusConfigModel.setOtpWithoutPassword(Set.of("PAP"));
        RadiusServerSettings radiusSettings = writeModelAndGetSettings();
        assertNotNull(radiusSettings);
        assertFalse(radiusSettings.isOtpWithoutPassword(ProtocolType.CHAP));
        assertFalse(radiusSettings.isOtpWithoutPassword(ProtocolType.MSCHAPV2));
        assertTrue(radiusSettings.isOtpWithoutPassword(ProtocolType.PAP));
    }

    @Test
    public void testOtpWithoutPasswordOnChapMsChapV2MixedCase() throws IOException {
        radiusConfigModel.setOtpWithoutPassword(Set.of("chap", "MSChapV2"));
        RadiusServerSettings radiusSettings = writeModelAndGetSettings();
        assertNotNull(radiusSettings);
        assertTrue(radiusSettings.isOtpWithoutPassword(ProtocolType.CHAP));
        assertTrue(radiusSettings.isOtpWithoutPassword(ProtocolType.MSCHAPV2));
        assertFalse(radiusSettings.isOtpWithoutPassword(ProtocolType.PAP));
    }

    @Test
    public void testOtpWithoutPasswordOWrongProtocol() throws IOException {
        // this test logs an ERROR about an invalid protocol setting
        radiusConfigModel.setOtpWithoutPassword(Set.of("chap", "foo"));
        RadiusServerSettings radiusSettings = writeModelAndGetSettings();
        assertNotNull(radiusSettings);
        assertTrue(radiusSettings.isOtpWithoutPassword(ProtocolType.CHAP));
        assertFalse(radiusSettings.isOtpWithoutPassword(ProtocolType.MSCHAPV2));
        assertFalse(radiusSettings.isOtpWithoutPassword(ProtocolType.PAP));
    }

    @Test
    public void testOtpWithoutPasswordLegacyOff() throws IOException {
        radiusConfigModel.setOtpWithoutPassword(null);
        RadiusServerSettings radiusSettings = writeModelAndGetSettings();
        assertNotNull(radiusSettings);
        assertFalse(radiusSettings.isOtpWithoutPassword(ProtocolType.CHAP));
        assertFalse(radiusSettings.isOtpWithoutPassword(ProtocolType.MSCHAPV2));
        assertFalse(radiusSettings.isOtpWithoutPassword(ProtocolType.PAP));
    }

    @Test
    public void testOtpWithoutPasswordOff() throws IOException {
        radiusConfigModel.setOtpWithoutPassword(null);
        RadiusServerSettings radiusSettings = writeModelAndGetSettings();
        assertNotNull(radiusSettings);
        assertFalse(radiusSettings.isOtpWithoutPassword(ProtocolType.CHAP));
        assertFalse(radiusSettings.isOtpWithoutPassword(ProtocolType.MSCHAPV2));
        assertFalse(radiusSettings.isOtpWithoutPassword(ProtocolType.PAP));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testOtpWithoutPasswordLegacyOn() throws IOException {
        // this test logs a WARNing about legacy otp setting
        radiusConfigModel.setOtpWithoutPassword(null);
        radiusConfigModel.setOtp(true);
        RadiusServerSettings radiusSettings = writeModelAndGetSettings();
        assertNotNull(radiusSettings);
        assertTrue(radiusSettings.isOtpWithoutPassword(ProtocolType.CHAP));
        assertTrue(radiusSettings.isOtpWithoutPassword(ProtocolType.MSCHAPV2));
        assertFalse(radiusSettings.isOtpWithoutPassword(ProtocolType.PAP));
    }
}
