package com.github.vzakharchenko.radius.radius.handlers;

import com.github.vzakharchenko.radius.providers.IRadiusProxyProvider;
import io.netty.channel.ChannelHandlerContext;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.server.RequestCtx;

import static com.github.vzakharchenko.radius.radius.handlers.session.KeycloakSessionUtils.getUser;

public abstract class AbstractHandler<T extends Provider>
        extends AbstractThreadRequestHandler
        implements ProviderFactory<T> {

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
        if (session != null && getUser(session) != null) {
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
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        this.sessionFactory = factory;
    }

    @Override
    public void close() {

    }
}
