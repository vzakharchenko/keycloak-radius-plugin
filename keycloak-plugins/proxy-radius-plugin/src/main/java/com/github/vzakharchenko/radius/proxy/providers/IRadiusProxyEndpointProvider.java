package com.github.vzakharchenko.radius.proxy.providers;

import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.Provider;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusEndpoint;

public interface IRadiusProxyEndpointProvider extends Provider {
    RadiusEndpoint getRadiusEndpoint(
            KeycloakSession session,
            Class<? extends RadiusPacket> packetType);
}
