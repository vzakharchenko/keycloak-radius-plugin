package ua.zaskarius.keycloak.plugins.radius.radius.provider;

import ua.zaskarius.keycloak.plugins.radius.configuration.ConfigurationScheduledTask;
import ua.zaskarius.keycloak.plugins.radius.configuration.RadiusConfigHelper;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class RadiusRadiusProviderFactory
        implements IRadiusProviderFactory<RadiusRadiusProvider> {


    public static final String KEYCLOAK_RADIUS_SERVER = "keycloak-radius-server";

    @Override
    public RadiusRadiusProvider create(KeycloakSession session) {
        return new RadiusRadiusProvider();
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        ConfigurationScheduledTask
                .addConnectionProviderMap(this);
        ConfigurationScheduledTask
                .addConfiguration(RadiusConfigHelper.getConfig());
    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return KEYCLOAK_RADIUS_SERVER;
    }
}
