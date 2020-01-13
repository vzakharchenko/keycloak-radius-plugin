package ua.zaskarius.keycloak.plugins.radius.configuration;

import com.google.common.annotations.VisibleForTesting;
import org.keycloak.util.JsonSerialization;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusAccessModel;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusConfigModel;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class FileRadiusConfiguration implements IRadiusConfiguration {

    private RadiusServerSettings radiusSettings = null;

    protected FileRadiusConfiguration() {
    }

    @Override
    public RadiusServerSettings getRadiusSettings() {
        if (radiusSettings == null) {
            File file = new File("config", "radius.config");
            if (!file.exists()) {
                throw new IllegalStateException(file.getAbsolutePath() + " does not exist");
            }

            try (InputStream fileInputStream = Files.newInputStream(Paths
                    .get(file.toURI()))) {
                RadiusConfigModel configModel = JsonSerialization
                        .readValue(fileInputStream, RadiusConfigModel.class);
                radiusSettings = transform(configModel);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return radiusSettings;
    }

    private RadiusServerSettings transform(RadiusConfigModel configModel) {
        RadiusServerSettings radiusServerSettings = new RadiusServerSettings();
        radiusServerSettings.setAccountPort(configModel.getAccountPort());
        radiusServerSettings.setAuthPort(configModel.getAuthPort());
        radiusServerSettings.setSecret(configModel.getSharedSecret());
        radiusServerSettings.setProvider(configModel.getProvider());
        radiusServerSettings.setUseRadius(configModel.isUseRadius());
        if (configModel.getRadiusIpAccess() != null) {

            radiusServerSettings
                    .setAccessMap(configModel.getRadiusIpAccess().stream().collect(
                            Collectors.toMap(RadiusAccessModel::getIp,
                                    RadiusAccessModel::getSharedSecret)));
        }
        return radiusServerSettings;
    }

    @VisibleForTesting
    public void setRadiusSettings(RadiusServerSettings radiusSettings) {
        this.radiusSettings = radiusSettings;
    }
}
