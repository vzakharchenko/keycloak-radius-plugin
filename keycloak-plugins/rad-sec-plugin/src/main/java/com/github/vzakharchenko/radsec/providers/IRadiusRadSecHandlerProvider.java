package com.github.vzakharchenko.radsec.providers;

import io.netty.channel.ChannelHandler;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.Provider;

public interface IRadiusRadSecHandlerProvider extends Provider {
    ChannelHandler getChannelHandler(KeycloakSession session);
}
