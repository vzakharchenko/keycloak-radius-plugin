package com.github.vzakharchenko.radius.providers;

import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

public class AccountingRadiusHandlerSPI implements Spi {

    public static final String ACCOUNT_RADIUS_SPI = "account-radius-spi";

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return ACCOUNT_RADIUS_SPI;
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return IRadiusAccountHandlerProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return IRadiusAccountHandlerProviderFactory.class;
    }
}
