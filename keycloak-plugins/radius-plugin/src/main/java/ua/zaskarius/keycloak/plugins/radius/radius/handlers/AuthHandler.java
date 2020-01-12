package ua.zaskarius.keycloak.plugins.radius.radius.handlers;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Promise;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.packet.RadiusPackets;
import org.tinyradius.server.handler.RequestHandler;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusUserInfo;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.AuthProtocol;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.RadiusAuthProtocolFactory;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.session.KeycloakSessionUtils;
import ua.zaskarius.keycloak.plugins.radius.transaction.KeycloakRadiusUtils;

import java.net.InetSocketAddress;
import java.util.List;

import static org.tinyradius.packet.PacketType.ACCESS_ACCEPT;
import static org.tinyradius.packet.PacketType.ACCESS_REJECT;

public class AuthHandler implements RequestHandler<AccessRequest,
        IKeycloakSecretProvider> {

    private final KeycloakSessionFactory sessionFactory;

    public AuthHandler(KeycloakSession session) {
        this.sessionFactory = session.getKeycloakSessionFactory();
    }

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
                                      InetSocketAddress remoteAddress,
                                      IKeycloakSecretProvider secretProvider) {
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
        request.getAttributes(33).forEach(answer::addAttribute);
        return answer;
    }

    @Override
    public Promise<RadiusPacket> handlePacket(Channel channel,
                                              AccessRequest request,
                                              InetSocketAddress remoteAddress,
                                              IKeycloakSecretProvider secretProvider) {

        return KeycloakRadiusUtils.runJobInTransaction(sessionFactory,
                threadSession -> {
                    Promise<RadiusPacket> promise = channel.eventLoop().newPromise();
                    try {
                        AuthProtocol authProtocol = RadiusAuthProtocolFactory
                                .getInstance()
                                .create(request, threadSession);
                        boolean isProtocolValid = authProtocol.isValid(remoteAddress);
                        RadiusPacket answer;
                        if (isProtocolValid) {
                            answer = handleAnswer(request,
                                    authProtocol, threadSession, remoteAddress, secretProvider);
                        } else {
                            answer = RadiusPackets.create(request.getDictionary(),
                                    ACCESS_REJECT, request.getIdentifier());
                        }
                        promise.trySuccess(answer);
                    } catch (Exception e) {
                        promise.tryFailure(e);
                    }
                    return promise;
                });
    }
}
