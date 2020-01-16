package ua.zaskarius.keycloak.plugins.radius.radius.handlers;

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
import org.tinyradius.server.handler.RequestHandler;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusUserInfo;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAuthHandlerProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAuthHandlerProviderFactory;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.AuthProtocol;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.RadiusAuthProtocolFactory;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.session.KeycloakSessionUtils;

import java.net.InetSocketAddress;
import java.util.List;

import static org.tinyradius.packet.PacketType.ACCESS_ACCEPT;
import static org.tinyradius.packet.PacketType.ACCESS_REJECT;

public class AuthHandler extends RequestHandler
        implements IRadiusAuthHandlerProviderFactory, IRadiusAuthHandlerProvider {

    public static final String DEFAULT_AUTH_RADIUS_PROVIDER = "default-auth-radius-provider";

    private static final Logger LOGGER = Logger.getLogger(AuthHandler.class);

    private KeycloakSessionFactory sessionFactory;

    private IKeycloakSecretProvider secretProvider;

    private boolean verifyPassword(AuthProtocol authProtocol,
                                   KeycloakSession session) {
        RadiusUserInfo radiusUserInfo = KeycloakSessionUtils.getRadiusUserInfo(session);
        if (radiusUserInfo != null) {
            List<String> passwords = radiusUserInfo.getPasswords();
            for (String password : passwords) {
                if (authProtocol.verifyPassword(password)) {
                    radiusUserInfo.setActivePassword(password);
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
        boolean init = secretProvider.init(remoteAddress, request.getUserName(),
                authProtocol,
                threadSession);
        RadiusPacket answer;
        if (init) {
            int type = verifyPassword(authProtocol, threadSession) ? ACCESS_ACCEPT :
                    ACCESS_REJECT;
            secretProvider.afterAuth(type,
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
        request.getAttributes(33).forEach(answer::addAttribute);
        ctx.writeAndFlush(msg.withResponse(answer));
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
            throw e;
        }
    }

    @Override
    public IRadiusAuthHandlerProvider create(KeycloakSession session) {
        this.sessionFactory = session.getKeycloakSessionFactory();
        return this;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

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
        secretProvider = new KeycloakSecretProvider();
        return (ChannelHandler) create(session);
    }

    @Override
    public void directRead(ChannelHandlerContext ctx, RequestCtx msg) {
        channelRead0(ctx, msg);
    }

    @VisibleForTesting
    public void setSecretProvider(IKeycloakSecretProvider secretProvider) {
        this.secretProvider = secretProvider;
    }
}
