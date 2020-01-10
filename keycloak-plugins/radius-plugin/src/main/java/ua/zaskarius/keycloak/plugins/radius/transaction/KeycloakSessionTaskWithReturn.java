package ua.zaskarius.keycloak.plugins.radius.transaction;

import org.keycloak.models.KeycloakSession;

public interface KeycloakSessionTaskWithReturn<T> {
    T run(KeycloakSession threadSession);
}
