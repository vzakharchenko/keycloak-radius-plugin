package ua.zaskarius.keycloak.plugins.radius.radius.holder;

import org.keycloak.common.ClientConnection;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.AuthProtocol;

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
}
