package ua.zaskarius.keycloak.plugins.radius.radius.handlers;

import ua.zaskarius.keycloak.plugins.radius.RadiusHelper;
import ua.zaskarius.keycloak.plugins.radius.configuration.RadiusConfigHelper;
import ua.zaskarius.keycloak.plugins.radius.event.log.EventLoggerFactory;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusUserInfo;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.clientconnection.RadiusClientConnection;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.AuthProtocol;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.session.KeycloakSessionUtils;
import ua.zaskarius.keycloak.plugins.radius.transaction.KeycloakRadiusUtils;
import org.jboss.logging.Logger;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.*;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static ua.zaskarius.keycloak.plugins.radius.mappers.RadiusPasswordMapper.RADIUS_SESSION_PASSWORD;
import static org.tinyradius.packet.PacketType.ACCESS_ACCEPT;

public class KeycloakSecretProvider implements IKeycloakSecretProvider {

    private static final Logger LOGGER = Logger
            .getLogger(KeycloakSecretProvider.class);

    private final KeycloakSessionFactory sessionFactory;

    public KeycloakSecretProvider(KeycloakSession session) {
        this.sessionFactory = session.getKeycloakSessionFactory();
    }

    @Override
    public String getSharedSecret(InetSocketAddress address) {
        return KeycloakRadiusUtils.runJobInTransaction(sessionFactory,
                threadSession -> {
                    List<RealmModel> reams = getRealmsByShared(address, threadSession);
                    if (reams.isEmpty()) {

                        LOGGER.warn("Radius Realm does not found for host : " +
                                address.getHostName());
                        EventBuilder event = EventLoggerFactory
                                .createMasterEvent(threadSession,
                                        new RadiusClientConnection(address));
                        event.event(EventType.LOGIN).detail(EventLoggerFactory.RADIUS_MESSAGE,
                                "Shared Secret not found. ")
                                .error("Shared Secret not found");
                        return null;
                    } else {
                        RealmModel realm = reams.get(0);
                        String secret = getSharedSecret(address, realm);
                        if (secret != null) {
                            EventBuilder event = EventLoggerFactory
                                    .createEvent(threadSession, realm,
                                            new RadiusClientConnection(address));
                            event.event(EventType.LOGIN).detail(EventLoggerFactory.RADIUS_MESSAGE,
                                    "Shared secret found.");
                        }
                        return secret;
                    }
                });

    }

    public List<RealmModel> getRealmsByShared(InetSocketAddress address,
                                              KeycloakSession localSession) {
        List<RealmModel> realms = new ArrayList<>();
        try {
            RealmProvider provider = localSession.getProvider(RealmProvider.class);
            for (RealmModel realm : provider.getRealms()) {
                RadiusServerSettings radiusSettings = RadiusConfigHelper.getConfig()
                        .getRadiusSettings(realm);

                if (radiusSettings != null
                        && radiusSettings.getUrl() != null &&
                        RadiusConfigHelper.getConfig().isUsedRadius(realm)) {
                    for (String url : radiusSettings.getUrl()) {
                        if (Objects.equals(url,
                                address.getAddress().getHostAddress())) {
                            realms.add(realm);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("getSharedSecret error", e);
            try {
                EventBuilder event = EventLoggerFactory
                        .createMasterEvent(localSession,
                                new RadiusClientConnection(address));
                event.event(EventType.LOGIN).detail(EventLoggerFactory.RADIUS_MESSAGE,
                        "Secret Error .").error(e.getMessage());
            } catch (RuntimeException ex) {
                LOGGER.error("send event error", e);
            } catch (Exception ex) {
                LOGGER.error("send event error", e);
            }
        }
        return realms;
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

    @Override
    public boolean init(InetSocketAddress address, String username,
                        AuthProtocol protocol,
                        KeycloakSession threadSession) {
        boolean successInit = false;
        RealmModel realm = protocol.getRealm();
        if (realm != null) {
            EventBuilder event = EventLoggerFactory
                    .createEvent(threadSession, realm,
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
                        realm));
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
        return successInit;
    }

    private String getSharedSecret(InetSocketAddress address,
                                   RealmModel realm) {
        RadiusServerSettings radiusSettings = RadiusConfigHelper
                .getConfig()
                .getRadiusSettings(realm);
        if (radiusSettings.getUrl().contains(address
                .getAddress()
                .getHostAddress())) {
            return radiusSettings.getSecret();
        } else {
            return null;
        }
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
