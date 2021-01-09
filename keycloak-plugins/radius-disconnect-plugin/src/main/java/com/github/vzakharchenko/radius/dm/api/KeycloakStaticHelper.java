package com.github.vzakharchenko.radius.dm.api;

import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.AccessToken;

public interface KeycloakStaticHelper {
     AccessToken getAccessToken(KeycloakSession keycloakSession);
}
