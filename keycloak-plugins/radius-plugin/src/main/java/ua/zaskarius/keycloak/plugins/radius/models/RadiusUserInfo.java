package ua.zaskarius.keycloak.plugins.radius.models;

import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.AuthProtocol;
import org.keycloak.common.ClientConnection;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.List;

public class RadiusUserInfo {
    private List<String> passwords;
    private UserModel userModel;
    private String activePassword;
    private String radiusSecret;
    private RealmModel realmModel;
    private AuthProtocol protocol;
    private ClientConnection clientConnection;

    public List<String> getPasswords() {
        return passwords;
    }

    public void setPasswords(List<String> passwords) {
        this.passwords = passwords;
    }

    public String getActivePassword() {
        return activePassword;
    }

    public void setActivePassword(String activePassword) {
        this.activePassword = activePassword;
    }

    public String getRadiusSecret() {
        return radiusSecret;
    }

    public void setRadiusSecret(String radiusSecret) {
        this.radiusSecret = radiusSecret;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public RealmModel getRealmModel() {
        return realmModel;
    }

    public void setRealmModel(RealmModel realmModel) {
        this.realmModel = realmModel;
    }

    public AuthProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(AuthProtocol protocol) {
        this.protocol = protocol;
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }
}
