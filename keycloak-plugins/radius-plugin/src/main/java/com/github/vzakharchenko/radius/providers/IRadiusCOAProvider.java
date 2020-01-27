package com.github.vzakharchenko.radius.providers;

import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.Provider;
import org.tinyradius.packet.AccountingRequest;

public interface IRadiusCOAProvider extends Provider {
    void initSession(AccountingRequest accountingRequest, KeycloakSession session, ClientModel client);

    void logout(AccountingRequest accountingRequest, KeycloakSession session);
}
