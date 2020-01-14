package ua.zaskarius.keycloak.plugins.radius.providers;

import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.Provider;
import org.tinyradius.packet.AccessRequest;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.KeycloakAttributes;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.KeycloakAttributesType;

public interface IRadiusAttributeProvider extends Provider {
    KeycloakAttributes createKeycloakAttributes(AccessRequest accessRequest,
                                                KeycloakSession session,
                                                KeycloakAttributesType type);
}
