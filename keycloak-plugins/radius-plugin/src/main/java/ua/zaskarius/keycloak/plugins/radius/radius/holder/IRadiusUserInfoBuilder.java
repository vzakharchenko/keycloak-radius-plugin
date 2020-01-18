package ua.zaskarius.keycloak.plugins.radius.radius.holder;

import org.keycloak.common.ClientConnection;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.AuthProtocol;

import java.util.List;

public interface IRadiusUserInfoBuilder {
    IRadiusUserInfoBuilder addPasswords(List<String> passwords);

    IRadiusUserInfoBuilder activePassword(String activePassword);

    IRadiusUserInfoBuilder radiusSecret(String radiusSecret);

    IRadiusUserInfoBuilder userModel(UserModel userModel);

    IRadiusUserInfoBuilder realmModel(RealmModel realmModel);

    IRadiusUserInfoBuilder protocol(AuthProtocol protocol);

    IRadiusUserInfoBuilder clientConnection(ClientConnection clientConnection);

    IRadiusUserInfoBuilder clientModel(ClientModel clientModel);

    IRadiusUserInfoGetter getRadiusUserInfoGetter();
}
