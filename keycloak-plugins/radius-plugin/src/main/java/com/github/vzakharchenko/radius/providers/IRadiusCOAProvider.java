package com.github.vzakharchenko.radius.providers;

import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.Provider;
import org.tinyradius.packet.AccountingRequest;

public interface IRadiusCOAProvider extends Provider {
    void initSession(AccountingRequest accountingRequest,
                     KeycloakSession session, String sessionId);

    void logout(AccountingRequest accountingRequest, KeycloakSession session);
}
