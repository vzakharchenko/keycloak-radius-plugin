package com.github.vzakharchenko.radius.transaction;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

public class KeycloakStaticHelper implements KeycloakHelper {

    @Override
    public AuthenticationManager.AuthResult getAuthResult(KeycloakSession session) {
        return new AppAuthManager().authenticateBearerToken(session,
                session.getContext().getRealm());
    }

}
