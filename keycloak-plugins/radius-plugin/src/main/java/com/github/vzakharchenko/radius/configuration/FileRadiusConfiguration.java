package com.github.vzakharchenko.radius.configuration;

import com.github.vzakharchenko.radius.models.CoASettings;
import com.github.vzakharchenko.radius.models.RadSecSettings;
import com.github.vzakharchenko.radius.models.RadiusServerSettings;
import com.github.vzakharchenko.radius.models.file.CoASettingsModel;
import com.github.vzakharchenko.radius.models.file.RadSecSettingsModel;
import com.github.vzakharchenko.radius.models.file.RadiusAccessModel;
import com.github.vzakharchenko.radius.models.file.RadiusConfigModel;
import com.github.vzakharchenko.radius.radius.handlers.protocols.ProtocolType;
import org.jboss.logging.Logger;
import org.keycloak.util.JsonSerialization;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.vzakharchenko.radius.radius.handlers.protocols.ProtocolType.CHAP;
import static com.github.vzakharchenko.radius.radius.handlers.protocols.ProtocolType.MSCHAPV2;
import static com.github.vzakharchenko.radius.radius.handlers.protocols.ProtocolType.PAP;

public class FileRadiusConfiguration implements IRadiusConfiguration {
    private static final Logger LOGGER = Logger.getLogger(FileRadiusConfiguration.class);
    public static final String FILE_VARIABLE = "KEYCLOAK_PATH";
    public static final String FILE_CONFIG_VARIABLE = "RADIUS_CONFIG_PATH";
    public static final String CONFIG = "config";

    private RadiusServerSettings radiusSettings;

    protected FileRadiusConfiguration() {
    }

    private File configPath() {
        return System.getenv(FILE_CONFIG_VARIABLE) != null ?
                new File(System.getenv(FILE_CONFIG_VARIABLE)) :
                new File(System.getenv(FILE_VARIABLE) != null ?
                        new File(System.getenv(FILE_VARIABLE), CONFIG).getAbsolutePath() : CONFIG);
    }

    @Override
    public RadiusServerSettings getRadiusSettings() {
        if (radiusSettings == null) {
            File file = new File(configPath(), "radius.config");
            if (!file.exists()) {
                throw new IllegalStateException(file.getAbsolutePath() + " does not exist");
            }

            try (InputStream fileInputStream = Files.newInputStream(Path
                    .of(file.toURI()))) {
                RadiusConfigModel configModel = JsonSerialization
                        .readValue(fileInputStream, RadiusConfigModel.class);
                radiusSettings = transform(configModel);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return radiusSettings;
    }

    // use for testing only
    public void setRadiusSettings(RadiusServerSettings radiusSettings) {
        this.radiusSettings = radiusSettings;
    }

    private RadSecSettings transform(RadSecSettingsModel radSecSettingsModel) {
        RadSecSettings radSecSettings = new RadSecSettings();
        if (radSecSettingsModel != null) {
            radSecSettings.setCert(radSecSettingsModel.getCertificate());
            radSecSettings.setPrivKey(radSecSettingsModel.getPrivateKey());
            radSecSettings.setUseRadSec(radSecSettingsModel.isUseRadSec());
            radSecSettings.setnThreads(radSecSettingsModel.getNumberThreads());
        }
        return radSecSettings;
    }

    private CoASettings transform(CoASettingsModel coASettingsModel) {
        CoASettings coASettings = new CoASettings();
        if (coASettingsModel != null) {
            coASettings.setCoaPort(coASettingsModel.getPort());
            coASettings.setUseCoAPackage(coASettingsModel.isUseCoA());
        }
        return coASettings;
    }

    private RadiusServerSettings transform(RadiusConfigModel configModel) {
        RadiusServerSettings radiusServerSettings = new RadiusServerSettings();
        radiusServerSettings.setAccountPort(configModel.getAccountPort());
        radiusServerSettings.setAuthPort(configModel.getAuthPort());
        radiusServerSettings.setSecret(configModel.getSharedSecret());
        radiusServerSettings.setNumberThreads(configModel.getNumberThreads());
        radiusServerSettings.setRadSecSettings(transform(configModel.getRadsec()));
        radiusServerSettings.setCoASettings(transform(configModel.getCoa()));
        radiusServerSettings.setUseUdpRadius(configModel.isUseUdpRadius());
        radiusServerSettings.setExternalDictionary(configModel.getExternalDictionary());

        transformOtpWithoutPassword(configModel, radiusServerSettings);

        if (configModel.getRadiusIpAccess() != null) {
            radiusServerSettings
                    .setAccessMap(configModel.getRadiusIpAccess().stream().collect(
                            Collectors.toMap(RadiusAccessModel::getIp,
                                    RadiusAccessModel::getSharedSecret)));
        }
        return radiusServerSettings;
    }

    @SuppressWarnings("deprecation")
    private static void transformOtpWithoutPassword(RadiusConfigModel configModel,
                                                    RadiusServerSettings radiusServerSettings) {
        Set<String> otpWithoutPassword = configModel.getOtpWithoutPassword();
        if (otpWithoutPassword.isEmpty() && configModel.isOtp()) {
            transformOtpLegacy(radiusServerSettings);
        } else {
            otpWithoutPassword.forEach(val -> {
                try {
                    radiusServerSettings.addOtpWithoutPassword(
                            ProtocolType.valueOf(val.toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException e) {
                    LOGGER.errorf("RADIUS configuration \"otpWithoutPassword\" contains " +
                            "unsupported value \"%1$s\", this value is ignored.", val);
                }
            });
            if (configModel.isOtp()) {
                LOGGER.warn("RADIUS configuration \"otp\":true is superseded by " +
                        "\"otpWithoutPassword\" setting, \"otp\" is ignored.");
            }
        }
    }

    private static void transformOtpLegacy(RadiusServerSettings radiusServerSettings) {
        // legacy support for pre 1.4.13 configuration
        LOGGER.warnf("RADIUS configuration \"otp\":true is superseded by " + //
                        "\"otpWithoutPassword\" setting, please update your configuration. " + //
                        "Instead \"otpWithoutPassword\":[\"%1$s\", \"%2$s\"] is used, " + //
                        "not \"%3$s\".",
                CHAP.name(), MSCHAPV2.name(), PAP.name());
        radiusServerSettings.addOtpWithoutPassword(CHAP);
        radiusServerSettings.addOtpWithoutPassword(MSCHAPV2);
    }
}
