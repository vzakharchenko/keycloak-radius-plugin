package ua.zaskarius.keycloak.plugins.radius.radius.handlers;

import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.AuthProtocol;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.server.SecretProvider;

import java.net.InetSocketAddress;

public interface IKeycloakSecretProvider extends SecretProvider {
    boolean init(InetSocketAddress address,
                 String username,
                 AuthProtocol protocol,
                 KeycloakSession threadSession);

    void afterAuth(int action,
                   KeycloakSession threadSession);
}
