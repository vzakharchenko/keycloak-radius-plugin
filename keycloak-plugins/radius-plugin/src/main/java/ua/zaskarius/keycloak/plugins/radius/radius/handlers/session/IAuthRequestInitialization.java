package ua.zaskarius.keycloak.plugins.radius.radius.handlers.session;

import org.keycloak.models.KeycloakSession;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.AuthProtocol;

import java.net.InetSocketAddress;

public interface IAuthRequestInitialization {
    boolean init(InetSocketAddress address,
                 String username,
                 AuthProtocol protocol,
                 KeycloakSession threadSession);

    void afterAuth(int action,
                   KeycloakSession threadSession);
}
