package com.github.vzakharchenko.radius.dm.api;

import org.keycloak.authorization.util.Tokens;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.AccessToken;

public class KeycloakStaticHelperImpl implements KeycloakStaticHelper {

    @Override
    public AccessToken getAccessToken(KeycloakSession keycloakSession) {
        return Tokens.getAccessToken(keycloakSession);
    }
}
