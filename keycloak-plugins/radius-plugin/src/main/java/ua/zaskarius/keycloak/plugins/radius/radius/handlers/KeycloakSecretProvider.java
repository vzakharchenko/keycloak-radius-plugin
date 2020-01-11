package ua.zaskarius.keycloak.plugins.radius.radius.handlers;

import org.jboss.logging.Logger;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.*;
import ua.zaskarius.keycloak.plugins.radius.RadiusHelper;
import ua.zaskarius.keycloak.plugins.radius.configuration.RadiusConfigHelper;
import ua.zaskarius.keycloak.plugins.radius.event.log.EventLoggerFactory;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusUserInfo;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.clientconnection.RadiusClientConnection;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.AuthProtocol;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.session.KeycloakSessionUtils;
import ua.zaskarius.keycloak.plugins.radius.transaction.KeycloakRadiusUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.tinyradius.packet.PacketType.ACCESS_ACCEPT;
import static ua.zaskarius.keycloak.plugins.radius.client.RadiusLoginProtocolFactory.RADIUS_PROTOCOL;
import static ua.zaskarius.keycloak.plugins.radius.mappers.RadiusPasswordMapper.RADIUS_SESSION_PASSWORD;

public class KeycloakSecretProvider implements IKeycloakSecretProvider {

    private static final Logger LOGGER = Logger.getLogger(KeycloakSecretProvider.class);

    private final KeycloakSessionFactory sessionFactory;

    public KeycloakSecretProvider(KeycloakSession session) {
        this.sessionFactory = session.getKeycloakSessionFactory();
    }

    @Override
    public String getSharedSecret(InetSocketAddress address) {
        return KeycloakRadiusUtils.runJobInTransaction(sessionFactory,
                threadSession -> getSharedSecret(address, threadSession));
    }


    private List<String> getSessionPasswords(
            KeycloakSession keycloakSession,
            RealmModel realmModel,
            UserModel userModel
    ) {
        List<String> passwords = new ArrayList<>();
        if (userModel.isEnabled()) {
            List<UserSessionModel> userSessions = keycloakSession.sessions()
                    .getUserSessions(realmModel, userModel);
            for (UserSessionModel userSession : userSessions) {
                String sessionNote = userSession.getNote(RADIUS_SESSION_PASSWORD);
                if (sessionNote != null) {
                    passwords.addAll(Arrays.asList(sessionNote.split(",")));
                }
            }
        }
        return passwords;
    }

    private RadiusUserInfo create(
            KeycloakSession keycloakSession,
            UserModel userModel,
            AuthProtocol protocol,
            RealmModel realmModel) {
        RadiusUserInfo radiusUserInfo = new RadiusUserInfo();
        radiusUserInfo.setRealmModel(realmModel);
        List<String> passwords = new ArrayList<>(
                getSessionPasswords(keycloakSession, realmModel, userModel));
        String currentPassword = RadiusHelper.getCurrentPassword(
                keycloakSession, realmModel, userModel
        );
        if (currentPassword != null && !currentPassword.isEmpty()) {
            passwords.add(currentPassword);
        }
        radiusUserInfo.setPasswords(passwords);
        radiusUserInfo.setUserModel(userModel);
        radiusUserInfo.setProtocol(protocol);
        return radiusUserInfo;
    }

    private UserModel getUserModel(KeycloakSession localSession,
                                   String username, RealmModel realm) {
        UserModel user = localSession.users().getUserByUsername(username, realm);
        if (user == null) {
            user = localSession.users().getUserByEmail(username, realm);
        }
        return user;
    }

    private ClientModel getClient(InetSocketAddress address,
                                  KeycloakSession session,
                                  RealmModel realmModel) {
        List<ClientModel> clients = realmModel.getClients();
        for (ClientModel client : clients) {
            if (Objects.equals(client.getProtocol(), RADIUS_PROTOCOL)) {
                return client;
            }
        }
        EventBuilder event = EventLoggerFactory
                .createEvent(session, realmModel,
                        new RadiusClientConnection(address));
        LOGGER.error("Client with radius protocol does not found");
        event.event(EventType.LOGIN_ERROR).detail(
                EventLoggerFactory.RADIUS_MESSAGE, "Client with radius protocol does not found")
                .error("Client with radius protocol does not found");
        return null;
    }

    @Override
    public boolean init(InetSocketAddress address, String username,
                        AuthProtocol protocol,
                        KeycloakSession threadSession) {
        boolean successInit = false;
        RealmModel realm = protocol.getRealm();
        if (realm != null) {
            ClientModel client = getClient(address, threadSession, realm);
            if (client != null) {
                EventBuilder event = EventLoggerFactory
                        .createEvent(threadSession, realm,
                                client,
                                new RadiusClientConnection(address));
                UserModel user = getUserModel(threadSession, username, realm);
                if (user != null && user.isEnabled()) {
                    RadiusUserInfo radiusUserInfo = create(
                            threadSession,
                            user,
                            protocol,
                            realm);
                    radiusUserInfo.setRadiusSecret(getSharedSecret(
                            address,
                            threadSession));
                    radiusUserInfo.setClientModel(client);
                    radiusUserInfo.setClientConnection(new RadiusClientConnection(address));
                    KeycloakSessionUtils.addRadiusUserInfo(threadSession, radiusUserInfo);
                    successInit = true;
                } else {
                    event.event(EventType.LOGIN_ERROR).detail(
                            EventLoggerFactory.RADIUS_MESSAGE, "USER DOES NOT EXIST")
                            .error("Login to RADIUS" +
                                    " fail for user " + username
                                    + ", user disabled or does not exists");
                }
            }

        }
        return successInit;
    }

    private String getSharedSecret(InetSocketAddress address, KeycloakSession session) {
        InetAddress inetAddress = address
                .getAddress();
        if (inetAddress == null) {
            return null;
        }
        String hostAddress = inetAddress
                .getHostAddress();
        RadiusServerSettings radiusSettings = RadiusConfigHelper
                .getConfig()
                .getRadiusSettings(session);
        String secret;
        if (radiusSettings.getAccessMap() != null) {
            secret = radiusSettings.getAccessMap().get(hostAddress);
            if (secret != null) {
                return secret;
            }
        }
        return radiusSettings.getSecret();
    }

    @Override
    public void afterAuth(int action,
                          InetSocketAddress address,
                          String username,
                          AuthProtocol authProtocol,
                          KeycloakSession threadSession) {

        if (authProtocol.getRealm() != null) {
            RadiusUserInfo radiusInfo = KeycloakSessionUtils
                    .getRadiusUserInfo(threadSession);
            if (radiusInfo != null) {
                RealmModel realm = radiusInfo.getRealmModel();
                EventBuilder event = EventLoggerFactory
                        .createEvent(threadSession, realm,
                                radiusInfo.getClientModel(),
                                new RadiusClientConnection(address));
                UserModel user = radiusInfo.getUserModel();
                if (action == ACCESS_ACCEPT) {
                    event.user(user);
                    event.event(EventType.LOGIN).detail("RADIUS", "success Login to RADIUS" +
                            " for user " + username).success();
                } else {
                    event.user(user);
                    event.event(EventType.LOGIN_ERROR).detail("RADIUS", "Login to RADIUS" +
                            " fail for user " + username
                            + ", please check password and try again").error("RADIUS ERROR");
                }
            }
        }

    }
}
