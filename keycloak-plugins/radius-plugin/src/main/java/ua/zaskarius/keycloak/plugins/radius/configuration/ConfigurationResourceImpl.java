package ua.zaskarius.keycloak.plugins.radius.configuration;

import com.google.common.annotations.VisibleForTesting;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.ForbiddenException;
import org.keycloak.services.managers.AuthenticationManager;
import ua.zaskarius.keycloak.plugins.radius.models.ConfigurationRepresentation;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusConfigModel;
import ua.zaskarius.keycloak.plugins.radius.transaction.KeycloakRadiusUtils;

public class ConfigurationResourceImpl implements ConfigurationResource {

    private IRadiusConfigJPA radiusConfigJPA;

    private AuthenticationManager.AuthResult auth;


    public ConfigurationResourceImpl(KeycloakSession session) {
        this.radiusConfigJPA = new RadiusConfigJPA(session);
        this.auth = KeycloakRadiusUtils
                .getKeycloakHelper()
                .getAuthResult(session);
        if (auth == null || auth.getUser() == null) {
            throw new ForbiddenException();
        }
    }

    @Override
    public ConfigurationRepresentation saveConfig(ConfigurationRepresentation configuration) {
        return transform(radiusConfigJPA
                .saveConfig(transform(configuration), auth.getUser()));
    }

    @Override
    public ConfigurationRepresentation getConfig() {
        RadiusConfigModel config = radiusConfigJPA.getConfig();

        if (config == null) {
            ConfigurationRepresentation configurationRepresentation =
                    new ConfigurationRepresentation();
            configurationRepresentation.setAccountPort(1813);
            configurationRepresentation.setAuthPort(1812);
            configurationRepresentation.setRadiusShared(null);
            configurationRepresentation.setStart(true);
            return saveConfig(configurationRepresentation);
        }
        return transform(config);
    }

    public ConfigurationRepresentation transform(RadiusConfigModel configModel) {
        ConfigurationRepresentation configurationRepresentation =
                new ConfigurationRepresentation();
        configurationRepresentation.setId(configModel.getId());
        configurationRepresentation.setAccountPort(configModel.getAccountPort());
        configurationRepresentation.setAuthPort(configModel.getAuthPort());
        configurationRepresentation.setRadiusShared(configModel.getRadiusShared());
        configurationRepresentation.setStart(configModel.isStart());
        return configurationRepresentation;
    }

    public RadiusConfigModel transform(ConfigurationRepresentation configurationRepresentation) {
        RadiusConfigModel configModel =
                new RadiusConfigModel();
        configModel.setId(configurationRepresentation.getId());
        configModel.setAccountPort(configurationRepresentation.getAccountPort());
        configModel.setAuthPort(configurationRepresentation.getAuthPort());
        configModel.setRadiusShared(configurationRepresentation.getRadiusShared());
        configModel.setStart(configurationRepresentation.isStart());
        return configModel;
    }

    @VisibleForTesting
    void setRadiusConfigJPA(IRadiusConfigJPA radiusConfigJPA) {
        this.radiusConfigJPA = radiusConfigJPA;
    }
}
