package com.github.vzakharchenko.radius.radius.handlers;

import com.github.vzakharchenko.radius.providers.IRadiusAccountHandlerProvider;
import com.github.vzakharchenko.radius.providers.IRadiusAccountHandlerProviderFactory;
import com.github.vzakharchenko.radius.radius.RadiusLibraryUtils;
import com.github.vzakharchenko.radius.radius.handlers.session.AccountingSessionManager;
import com.github.vzakharchenko.radius.radius.handlers.session.IAccountingSessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.packet.RadiusPackets;
import org.tinyradius.server.RequestCtx;
import org.tinyradius.server.handler.RequestHandler;

import static org.tinyradius.packet.PacketType.ACCOUNTING_RESPONSE;

public class AccountingHandler
        extends RequestHandler
        implements IRadiusAccountHandlerProviderFactory, IRadiusAccountHandlerProvider {


    public static final String DEFAULT_ACCOUNT_RADIUS_PROVIDER = "default-account-radius-provider";

    private KeycloakSessionFactory sessionFactory;

    @Override
    protected Class<AccountingRequest> acceptedPacketType() {
        return AccountingRequest.class;
    }

    private boolean isRequestFromRadius(RadiusPacket request) {
        String acctType = RadiusLibraryUtils
                .getAttributeValue(request,
                        "Acct-Authentic");
        return (acctType.isEmpty() || !"Local"
                .equalsIgnoreCase(acctType));
    }

    private void successResponse(ChannelHandlerContext ctx,
                                 RequestCtx msg,
                                 RadiusPacket request) {
        RadiusPacket answer = RadiusPackets.create(request.getDictionary(),
                ACCOUNTING_RESPONSE, request.getIdentifier());
        request.getAttributes(33).forEach(answer::addAttribute);
        ctx.writeAndFlush(msg.withResponse(answer));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestCtx msg) {
        final RadiusPacket request = msg.getRequest();

        if (isRequestFromRadius(request)) {
            KeycloakModelUtils.runJobInTransaction(sessionFactory, session -> {
                IAccountingSessionManager manageSession = new AccountingSessionManager(
                        (AccountingRequest) request,
                        session,
                        msg.getEndpoint()
                ).init().updateContext().manageSession();
                if (!manageSession.isValidSession()) {
                    manageSession.logout();
                }
                successResponse(ctx, msg, request);
            });
        } else {
            successResponse(ctx, msg, request);
        }
    }

    @Override
    public IRadiusAccountHandlerProvider create(KeycloakSession session) {
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
    public String getId() {
        return DEFAULT_ACCOUNT_RADIUS_PROVIDER;
    }

    @Override
    public ChannelHandler getChannelHandler(KeycloakSession session) {
        return (ChannelHandler) create(session);
    }

    @Override
    public void directRead(ChannelHandlerContext ctx, RequestCtx msg) {
        channelRead0(ctx, msg);
    }
}
