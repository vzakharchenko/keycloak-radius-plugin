package com.github.vzakharchenko.radius.radius.holder;

import com.github.vzakharchenko.radius.radius.handlers.protocols.AuthProtocol;
import org.keycloak.common.ClientConnection;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.net.InetSocketAddress;
import java.util.List;

/* default */ class RadiusUserInfo implements IRadiusUserInfo {
    private List<String> passwords;
    private UserModel userModel;
    private String activePassword;
    private String radiusSecret;
    private RealmModel realmModel;
    private AuthProtocol protocol;
    private ClientModel clientModel;
    private ClientConnection clientConnection;
    private InetSocketAddress address;

    @Override
    public List<String> getPasswords() {
        return passwords;
    }

    public void setPasswords(List<String> passwords) {
        this.passwords = passwords;
    }

    @Override
    public String getActivePassword() {
        return activePassword;
    }

    public void setActivePassword(String activePassword) {
        this.activePassword = activePassword;
    }

    @Override
    public String getRadiusSecret() {
        return radiusSecret;
    }

    public void setRadiusSecret(String radiusSecret) {
        this.radiusSecret = radiusSecret;
    }

    @Override
    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    @Override
    public RealmModel getRealmModel() {
        return realmModel;
    }

    public void setRealmModel(RealmModel realmModel) {
        this.realmModel = realmModel;
    }

    @Override
    public AuthProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(AuthProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    @Override
    public ClientModel getClientModel() {
        return clientModel;
    }

    public void setClientModel(ClientModel clientModel) {
        this.clientModel = clientModel;
    }

    @Override
    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }
}
