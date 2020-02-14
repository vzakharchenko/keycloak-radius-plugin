package com.github.vzakharchenko.radius.radius.holder;

import com.github.vzakharchenko.radius.radius.handlers.protocols.AuthProtocol;
import org.keycloak.common.ClientConnection;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.net.InetSocketAddress;
import java.util.List;

public interface IRadiusUserInfo {
    List<String> getPasswords();

    String getActivePassword();

    String getRadiusSecret();

    UserModel getUserModel();

    RealmModel getRealmModel();

    AuthProtocol getProtocol();

    ClientConnection getClientConnection();

    ClientModel getClientModel();

    InetSocketAddress getAddress();

    boolean isForceReject();
}
