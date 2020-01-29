package com.github.vzakharchenko.radius.radius.handlers;

import com.github.vzakharchenko.radius.providers.IRadiusProxyProvider;
import io.netty.channel.ChannelHandlerContext;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.server.RequestCtx;
import org.tinyradius.server.handler.RequestHandler;

public abstract class AbstractHandler extends RequestHandler {

    private void callProxy(
            KeycloakSession session,
            RequestCtx msg,
            RadiusPacket answer) {
        IRadiusProxyProvider proxyProvider = session.getProvider(IRadiusProxyProvider.class);
        if (proxyProvider != null) {
            proxyProvider.proxy(session, msg, answer);
        }
    }


    protected void radiusResponse(
            KeycloakSession session,
            ChannelHandlerContext ctx,
            RequestCtx msg,
            RadiusPacket answer
    ) {
        if (session != null) {
            callProxy(session, msg, answer);
        }
        msg.getRequest().getAttributes(33).forEach(answer::addAttribute);
        ctx.writeAndFlush(msg.withResponse(answer));
    }

    protected void radiusResponse(
            ChannelHandlerContext ctx,
            RequestCtx msg,
            RadiusPacket answer
    ) {
        radiusResponse(null, ctx, msg, answer);
    }
}
