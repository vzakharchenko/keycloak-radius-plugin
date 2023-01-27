package com.github.vzakharchenko.radius.radius.handlers.session;

import com.github.vzakharchenko.radius.RadiusHelper;
import com.github.vzakharchenko.radius.event.log.EventLoggerUtils;
import com.github.vzakharchenko.radius.mappers.RadiusSessionPasswordManager;
import com.github.vzakharchenko.radius.radius.RadiusLibraryUtils;
import com.github.vzakharchenko.radius.radius.handlers.clientconnection.RadiusClientConnection;
import com.github.vzakharchenko.radius.radius.handlers.protocols.AuthProtocol;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfo;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoBuilder;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoGetter;
import com.github.vzakharchenko.radius.radius.holder.RadiusUserInfoBuilder;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.*;
import org.tinyradius.server.SecretProvider;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import static org.tinyradius.packet.PacketType.ACCESS_ACCEPT;
import static org.tinyradius.packet.PacketType.ACCESS_REJECT;

public class AuthRequestInitialization implements IAuthRequestInitialization {
    private final SecretProvider secretProvider;

    public AuthRequestInitialization(SecretProvider secretProvider) {
        this.secretProvider = secretProvider;
    }

    private List<PasswordData> getSessionPasswords(
            KeycloakSession keycloakSession,
            RealmModel realmModel,
            UserModel userModel
    ) {
        List<PasswordData> passwords = new ArrayList<>();
        if (userModel.isEnabled()) {
            keycloakSession.sessions().getUserSessionsStream(realmModel, userModel)
                .forEach(userSession -> {
                    String sessionPassword = RadiusSessionPasswordManager.getInstance()
                            .getCurrentPassword(userSession);
                    if (sessionPassword != null) {
                        passwords.add(PasswordData.create(sessionPassword, true));
                    }
                });
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
        List<PasswordData> passwords = new ArrayList<>(
                getSessionPasswords(keycloakSession, realmModel, userModel));
        String currentPassword = RadiusHelper.getCurrentPassword(userModel
        );
        if (currentPassword != null && !currentPassword.isEmpty()) {
            passwords.add(PasswordData.create(currentPassword));
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
                    .address(clientConnection.getInetSocketAddress())
                    .protocol(protocol)
                    .addPassword(RadiusLibraryUtils.getServiceAccountPassword(user, realm))
                    .getRadiusUserInfoGetter();
            KeycloakSessionUtils.addRadiusUserInfo(threadSession, radiusUserInfoGetter);
            KeycloakSessionUtils.context(threadSession, radiusUserInfoGetter);
            return true;
        } else {
            event.event(EventType.LOGIN_ERROR).detail(
                    EventLoggerUtils.RADIUS_MESSAGE, "USER DOES NOT EXIST")
                    .error("Login to RADIUS " + username + ", user disabled or does not exists");
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
