package ua.zaskarius.keycloak.plugins.radius.radius.handlers.session;

import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.*;
import org.tinyradius.server.SecretProvider;
import ua.zaskarius.keycloak.plugins.radius.RadiusHelper;
import ua.zaskarius.keycloak.plugins.radius.event.log.EventLoggerUtils;
import ua.zaskarius.keycloak.plugins.radius.radius.RadiusLibraryUtils;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.clientconnection.RadiusClientConnection;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.AuthProtocol;
import ua.zaskarius.keycloak.plugins.radius.radius.holder.IRadiusUserInfo;
import ua.zaskarius.keycloak.plugins.radius.radius.holder.IRadiusUserInfoBuilder;
import ua.zaskarius.keycloak.plugins.radius.radius.holder.IRadiusUserInfoGetter;
import ua.zaskarius.keycloak.plugins.radius.radius.holder.RadiusUserInfoBuilder;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.tinyradius.packet.PacketType.ACCESS_ACCEPT;
import static org.tinyradius.packet.PacketType.ACCESS_REJECT;
import static ua.zaskarius.keycloak.plugins.radius.mappers.RadiusPasswordMapper.RADIUS_SESSION_PASSWORD;

public class AuthRequestInitialization implements IAuthRequestInitialization {
    private final SecretProvider secretProvider;

    public AuthRequestInitialization(SecretProvider secretProvider) {
        this.secretProvider = secretProvider;
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

    private IRadiusUserInfoBuilder create(
            KeycloakSession keycloakSession,
            UserModel userModel,
            RealmModel realmModel,
            ClientModel client) {
        IRadiusUserInfoBuilder radiusUserInfoBuilder = RadiusUserInfoBuilder.create();
        radiusUserInfoBuilder.realmModel(realmModel);
        List<String> passwords = new ArrayList<>(
                getSessionPasswords(keycloakSession, realmModel, userModel));
        String currentPassword = RadiusHelper.getCurrentPassword(
                keycloakSession, realmModel, userModel
        );
        if (currentPassword != null && !currentPassword.isEmpty()) {
            passwords.add(currentPassword);
        }
        return radiusUserInfoBuilder.addPasswords(passwords).userModel(userModel)
                .clientModel(client);
    }

    //CHECKSTYLE:OFF
    private boolean init(RadiusClientConnection clientConnection,
                         String username,
                         AuthProtocol protocol,
                         KeycloakSession threadSession,
                         RealmModel realm,
                         ClientModel client) {
        //CHECKSTYLE:ON
        EventBuilder event = EventLoggerUtils
                .createEvent(threadSession, realm, client, clientConnection);
        UserModel user = RadiusLibraryUtils.getUserModel(threadSession, username, realm);
        if (user != null && user.isEnabled()) {
            IRadiusUserInfoGetter radiusUserInfoGetter = create(
                    threadSession, user, realm, client
            ).radiusSecret(secretProvider.getSharedSecret(clientConnection.getInetSocketAddress()))
                    .clientConnection(clientConnection)
                    .protocol(protocol).getRadiusUserInfoGetter();
            KeycloakSessionUtils.addRadiusUserInfo(threadSession, radiusUserInfoGetter);
            return true;
        } else {
            event.event(EventType.LOGIN_ERROR).detail(
                    EventLoggerUtils.RADIUS_MESSAGE, "USER DOES NOT EXIST")
                    .error("Login to RADIUS" + " fail for user " + username
                            + ", user disabled or does not exists");
        }
        return false;
    }


    @Override
    public boolean init(InetSocketAddress address,
                        String username,
                        AuthProtocol protocol,
                        KeycloakSession threadSession) {
        boolean successInit = false;
        RealmModel realm = protocol.getRealm();
        if (realm != null) {
            RadiusClientConnection clientConnection = new RadiusClientConnection(
                    address, protocol.getAccessRequest());
            ClientModel client = RadiusLibraryUtils.getClient(clientConnection,
                    threadSession, realm);
            if (client != null) {
                successInit = init(clientConnection, username, protocol,
                        threadSession, realm, client);
            }
        }
        return successInit;
    }


    @Override
    public void afterAuth(int action,
                          KeycloakSession threadSession) {

        IRadiusUserInfoGetter radiusUserInfoGetter = KeycloakSessionUtils
                .getRadiusUserInfo(threadSession);
        if (radiusUserInfoGetter != null) {
            IRadiusUserInfo radiusInfo = radiusUserInfoGetter.getRadiusUserInfo();
            RealmModel realm = radiusInfo.getRealmModel();
            EventBuilder event = EventLoggerUtils
                    .createEvent(threadSession, realm,
                            radiusInfo.getClientModel(), radiusInfo.getClientConnection());
            UserModel user = radiusInfo.getUserModel();
            if (action == ACCESS_ACCEPT) {
                event.user(user);
                event.event(EventType.LOGIN).detail("RADIUS", "success Login to RADIUS" +
                        " for user " + user.getUsername()).success();
            } else if (action == ACCESS_REJECT) {
                event.user(user);
                event.event(EventType.LOGIN_ERROR).detail("RADIUS", "Login to RADIUS" +
                        " fail for user " + user.getUsername()
                        + ", please check password and try again").error("RADIUS ERROR");
            }
        }
    }
}
