package com.github.vzakharchenko.radius.radius.handlers;

import com.github.vzakharchenko.radius.providers.IRadiusAuthHandlerProvider;
import com.github.vzakharchenko.radius.providers.IRadiusAuthHandlerProviderFactory;
import com.github.vzakharchenko.radius.radius.handlers.protocols.AuthProtocol;
import com.github.vzakharchenko.radius.radius.handlers.protocols.RadiusAuthProtocolFactory;
import com.github.vzakharchenko.radius.radius.handlers.session.AuthRequestInitialization;
import com.github.vzakharchenko.radius.radius.handlers.session.IAuthRequestInitialization;
import com.github.vzakharchenko.radius.radius.handlers.session.KeycloakSessionUtils;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoGetter;
import com.google.common.annotations.VisibleForTesting;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.packet.RadiusPackets;
import org.tinyradius.server.RequestCtx;

import java.net.InetSocketAddress;
import java.util.List;

import static org.tinyradius.packet.PacketType.ACCESS_ACCEPT;
import static org.tinyradius.packet.PacketType.ACCESS_REJECT;

public class AuthHandler extends AbstractHandler
        implements IRadiusAuthHandlerProviderFactory, IRadiusAuthHandlerProvider {

    public static final String DEFAULT_AUTH_RADIUS_PROVIDER = "default-auth-radius-provider";

    private static final Logger LOGGER = Logger.getLogger(AuthHandler.class);

    private KeycloakSessionFactory sessionFactory;

    private IAuthRequestInitialization authRequestInitialization;

    private boolean verifyPassword(AuthProtocol authProtocol,
                                   KeycloakSession session) {
        IRadiusUserInfoGetter radiusUserInfoGetter = KeycloakSessionUtils
                .getRadiusUserInfo(session);
        if (radiusUserInfoGetter != null) {
            List<String> passwords = radiusUserInfoGetter.getRadiusUserInfo().getPasswords();
            for (String password : passwords) {
                if (authProtocol.verifyPassword(password)) {
                    radiusUserInfoGetter.getBuilder().activePassword(password);
                    return true;
                }
            }
        }
        return false;
    }

    private RadiusPacket handleAnswer(AccessRequest request,
                                      AuthProtocol authProtocol,
                                      KeycloakSession threadSession,
                                      InetSocketAddress remoteAddress) {
        boolean init = authRequestInitialization.init(remoteAddress, request.getUserName(),
                authProtocol,
                threadSession);
        RadiusPacket answer;
        if (init) {
            int type = verifyPassword(authProtocol, threadSession) ? ACCESS_ACCEPT :
                    ACCESS_REJECT;
            authRequestInitialization.afterAuth(type,
                    threadSession);
            answer = RadiusPackets.create(request.getDictionary(),
                    type, request.getIdentifier());
            if (type != ACCESS_REJECT) {
                authProtocol.prepareAnswer(answer);
            }
        } else {
            answer = RadiusPackets.create(request.getDictionary(),
                    ACCESS_REJECT, request.getIdentifier());
        }
        return answer;
    }


    @Override
    protected Class<? extends RadiusPacket> acceptedPacketType() {
        return AccessRequest.class;
    }


    protected void channelRead0(ChannelHandlerContext ctx, RequestCtx msg,
                                KeycloakSession threadSession) {
        RadiusPacket answer;
        final AccessRequest request = (AccessRequest) msg.getRequest();
        try {
            AuthProtocol authProtocol = RadiusAuthProtocolFactory
                    .getInstance().create(request, threadSession);
            InetSocketAddress address = msg.getEndpoint().getAddress();
            boolean isProtocolValid = authProtocol.isValid(address);
            answer = isProtocolValid ? handleAnswer(request,
                    authProtocol, threadSession, address) : RadiusPackets
                    .create(request.getDictionary(), ACCESS_REJECT,
                            request.getIdentifier());
        } catch (RuntimeException e) {
            LOGGER.error("failed with message", e);
            answer = RadiusPackets.create(request.getDictionary(),
                    ACCESS_REJECT, request.getIdentifier());
        }
        radiusResponse(threadSession, ctx, msg, answer);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestCtx msg) {
        try {
            KeycloakModelUtils.runJobInTransaction(sessionFactory,
                    threadSession -> {
                        channelRead0(ctx, msg, threadSession);
                    });
        } catch (RuntimeException e) {
            LOGGER.error("failed request", e);
            radiusResponse(ctx, msg, RadiusPackets.create(msg.getRequest().getDictionary(),
                    ACCESS_REJECT, msg.getRequest().getIdentifier()));
        }
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
        this.authRequestInitialization = new AuthRequestInitialization(
                new KeycloakSecretProvider());
    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return DEFAULT_AUTH_RADIUS_PROVIDER;
    }

    @Override
    public ChannelHandler getChannelHandler(KeycloakSession session) {
        return (ChannelHandler) create(session);
    }

    @Override
    public void directRead(ChannelHandlerContext ctx, RequestCtx msg) {
        channelRead0(ctx, msg);
    }

    @VisibleForTesting
    public void setAuthRequestInitialization(
            IAuthRequestInitialization authRequestInitialization) {
        this.authRequestInitialization = authRequestInitialization;
    }
}
