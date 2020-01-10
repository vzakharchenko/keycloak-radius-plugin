package ua.zaskarius.keycloak.plugins.radius.transaction;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.AuthenticationManager;

public interface KeycloakHelper {

    AuthenticationManager.AuthResult getAuthResult(KeycloakSession session);
}
