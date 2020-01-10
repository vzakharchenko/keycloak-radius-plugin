package ua.zaskarius.keycloak.plugins.radius.providers;

import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.KeycloakAttributes;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.KeycloakAttributesType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.Provider;

public interface IRadiusAttributeProvider extends Provider {
    KeycloakAttributes createKeycloakAttributes(KeycloakSession session,
                                                KeycloakAttributesType type);
}
