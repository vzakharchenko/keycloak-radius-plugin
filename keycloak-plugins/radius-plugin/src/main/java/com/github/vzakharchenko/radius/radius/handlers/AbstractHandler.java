package com.github.vzakharchenko.radius.radius.handlers;

import com.github.vzakharchenko.radius.providers.IRadiusAuthHandlerProvider;
import com.github.vzakharchenko.radius.providers.IRadiusAuthHandlerProviderFactory;
import com.github.vzakharchenko.radius.providers.IRadiusProxyProvider;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.server.RequestCtx;

public abstract class AbstractHandler
        extends AbstractThreadRequestHandler
        implements IRadiusAuthHandlerProviderFactory,
        IRadiusAuthHandlerProvider {

    protected KeycloakSessionFactory sessionFactory;

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

    @Override
    public IRadiusAuthHandlerProvider create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        this.sessionFactory = factory;
    }

    @Override
    public void close() {

    }

    @Override
    public ChannelHandler getChannelHandler(KeycloakSession session) {
        return (ChannelHandler) create(session);
    }

    @Override
    public void directRead(ChannelHandlerContext ctx, RequestCtx msg) {
        channelReadRadius(ctx, msg);
    }
}
