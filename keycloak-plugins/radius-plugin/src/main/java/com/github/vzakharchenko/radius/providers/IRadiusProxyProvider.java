package com.github.vzakharchenko.radius.providers;

import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.Provider;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.server.RequestCtx;

public interface IRadiusProxyProvider extends Provider {
    RadiusPacket proxy(KeycloakSession session, RequestCtx msg, RadiusPacket answer);
}
