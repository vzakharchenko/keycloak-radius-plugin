package com.github.vzakharchenko.radius.providers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.Provider;
import org.tinyradius.server.RequestCtx;

public interface IRadiusAuthHandlerProvider extends Provider {
    ChannelHandler getChannelHandler(KeycloakSession session);

    void directRead(ChannelHandlerContext ctx, RequestCtx msg);
}
