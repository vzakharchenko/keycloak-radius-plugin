package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class ConfigurationEndpointFactory implements RealmResourceProviderFactory {

    public static final String RADIUS_CONFIGURATION_ENDPOINT = "Radius-configuration-endpoint";

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new ConfigurationProvider(session);
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return RADIUS_CONFIGURATION_ENDPOINT;
    }
}
