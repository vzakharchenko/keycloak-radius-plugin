package com.github.vzakharchenko.radius.proxy.proxy;

import org.keycloak.models.KeycloakSession;
import org.tinyradius.packet.RadiusPacket;

public interface IComponentProxy {
    RadiusPacket proxy(KeycloakSession session, RadiusPacket radiusPacket, RadiusPacket answer);
}
