package com.github.vzakharchenko.radsec.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import org.keycloak.models.KeycloakSession;

public interface IRadSecServerProvider {
    ChannelHandler createHandler(Channel ch);

    ChannelHandler radsecChannel(KeycloakSession session);
}
