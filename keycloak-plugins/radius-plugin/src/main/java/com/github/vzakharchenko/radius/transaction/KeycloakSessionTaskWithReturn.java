package com.github.vzakharchenko.radius.transaction;

import org.keycloak.models.KeycloakSession;

public interface KeycloakSessionTaskWithReturn<T> {
    T run(KeycloakSession threadSession);
}
