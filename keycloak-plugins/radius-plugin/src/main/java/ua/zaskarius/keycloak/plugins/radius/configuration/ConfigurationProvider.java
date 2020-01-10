package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

public class ConfigurationProvider implements RealmResourceProvider {

    private final KeycloakSession session;

    public ConfigurationProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return new ConfigurationResourceImpl(session);
    }

    @Override
    public void close() {

    }
}
