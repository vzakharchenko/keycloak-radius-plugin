package ua.zaskarius.keycloak.plugins.radius.radius.holder;

import org.keycloak.common.ClientConnection;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.AuthProtocol;

import java.util.ArrayList;
import java.util.List;

public final class RadiusUserInfoBuilder implements IRadiusUserInfoBuilder,
        IRadiusUserInfoGetter {
    private final RadiusUserInfo radiusUserInfo;

    private RadiusUserInfoBuilder() {
        radiusUserInfo = new RadiusUserInfo();
    }

    @Override
    public IRadiusUserInfoBuilder addPasswords(List<String> passwords) {
        List<String> infoPasswords = radiusUserInfo.getPasswords();
        if (infoPasswords == null) {
            infoPasswords = new ArrayList<>();
            radiusUserInfo.setPasswords(infoPasswords);
        }
        infoPasswords.addAll(passwords);
        return this;
    }

    @Override
    public IRadiusUserInfoBuilder activePassword(String activePassword) {
        radiusUserInfo.setActivePassword(activePassword);
        return this;

    }

    @Override
    public IRadiusUserInfoBuilder radiusSecret(String radiusSecret) {
        radiusUserInfo.setRadiusSecret(radiusSecret);
        return this;
    }

    @Override
    public IRadiusUserInfoBuilder userModel(UserModel userModel) {
        radiusUserInfo.setUserModel(userModel);
        return this;
    }

    @Override
    public IRadiusUserInfoBuilder realmModel(RealmModel realmModel) {
        radiusUserInfo.setRealmModel(realmModel);
        return this;
    }

    @Override
    public IRadiusUserInfoBuilder protocol(AuthProtocol protocol) {
        radiusUserInfo.setProtocol(protocol);
        return this;
    }

    @Override
    public IRadiusUserInfoBuilder clientConnection(ClientConnection clientConnection) {
        radiusUserInfo.setClientConnection(clientConnection);
        return this;
    }

    @Override
    public IRadiusUserInfoBuilder clientModel(ClientModel clientModel) {
        radiusUserInfo.setClientModel(clientModel);
        return this;
    }

    public static IRadiusUserInfoBuilder create() {
        return new RadiusUserInfoBuilder();
    }

    @Override
    public IRadiusUserInfo getRadiusUserInfo() {
        return radiusUserInfo;
    }

    @Override
    public IRadiusUserInfoBuilder getBuilder() {
        return this;
    }

    @Override
    public IRadiusUserInfoGetter getRadiusUserInfoGetter() {
        return this;
    }
}
