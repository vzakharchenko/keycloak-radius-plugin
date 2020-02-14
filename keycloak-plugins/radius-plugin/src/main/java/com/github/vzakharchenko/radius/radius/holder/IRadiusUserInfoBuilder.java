package com.github.vzakharchenko.radius.radius.holder;

import com.github.vzakharchenko.radius.radius.handlers.protocols.AuthProtocol;
import org.keycloak.common.ClientConnection;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.net.InetSocketAddress;
import java.util.List;

public interface IRadiusUserInfoBuilder {
    IRadiusUserInfoBuilder addPasswords(List<String> passwords);

    IRadiusUserInfoBuilder activePassword(String activePassword);

    IRadiusUserInfoBuilder radiusSecret(String radiusSecret);

    IRadiusUserInfoBuilder userModel(UserModel userModel);

    IRadiusUserInfoBuilder realmModel(RealmModel realmModel);

    IRadiusUserInfoBuilder protocol(AuthProtocol protocol);

    IRadiusUserInfoBuilder clientConnection(ClientConnection clientConnection);

    IRadiusUserInfoBuilder address(InetSocketAddress address);

    IRadiusUserInfoBuilder clientModel(ClientModel clientModel);

    IRadiusUserInfoBuilder forceReject();

    IRadiusUserInfoGetter getRadiusUserInfoGetter();
}
