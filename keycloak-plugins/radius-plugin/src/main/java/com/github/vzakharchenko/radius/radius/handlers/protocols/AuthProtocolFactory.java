package com.github.vzakharchenko.radius.radius.handlers.protocols;

import org.keycloak.models.KeycloakSession;
import org.tinyradius.packet.AccessRequest;

public interface AuthProtocolFactory {
    AuthProtocol create(AccessRequest accessRequest, KeycloakSession session);
}
