package com.github.vzakharchenko.radius.providers;

import com.github.vzakharchenko.radius.radius.handlers.attributes.KeycloakAttributes;
import com.github.vzakharchenko.radius.radius.handlers.attributes.KeycloakAttributesType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.Provider;
import org.tinyradius.packet.AccessRequest;

public interface IRadiusAttributeProvider extends Provider {
    KeycloakAttributes createKeycloakAttributes(AccessRequest accessRequest,
                                                KeycloakSession session,
                                                KeycloakAttributesType type);
}
