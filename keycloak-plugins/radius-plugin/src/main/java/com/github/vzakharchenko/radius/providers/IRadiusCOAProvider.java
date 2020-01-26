package com.github.vzakharchenko.radius.providers;

import org.keycloak.provider.Provider;
import org.tinyradius.packet.AccountingRequest;

public interface IRadiusCOAProvider extends Provider {
    void initSession(AccountingRequest accountingRequest);

    void logout(AccountingRequest accountingRequest);
}
