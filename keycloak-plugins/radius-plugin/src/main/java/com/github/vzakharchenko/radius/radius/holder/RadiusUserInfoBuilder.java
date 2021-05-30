package com.github.vzakharchenko.radius.radius.holder;

import com.github.vzakharchenko.radius.radius.handlers.protocols.AuthProtocol;
import org.keycloak.common.ClientConnection;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RadiusUserInfoBuilder implements IRadiusUserInfoBuilder,
        IRadiusUserInfoGetter {
    private final RadiusUserInfo radiusUserInfo;

    private RadiusUserInfoBuilder() {
        radiusUserInfo = new RadiusUserInfo();
    }

    public static IRadiusUserInfoBuilder create() {
        return new RadiusUserInfoBuilder();
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
    public IRadiusUserInfoBuilder addPassword(String password) {
        return password != null ? addPasswords(Collections
                .singletonList(password)) : this;
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
    public IRadiusUserInfoBuilder address(InetSocketAddress address) {
        radiusUserInfo.setAddress(address);
        return this;
    }

    @Override
    public IRadiusUserInfoBuilder clientModel(ClientModel clientModel) {
        radiusUserInfo.setClientModel(clientModel);
        return this;
    }

    @Override
    public IRadiusUserInfoBuilder forceReject() {
        radiusUserInfo.setForceReject(true);
        return this;
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
