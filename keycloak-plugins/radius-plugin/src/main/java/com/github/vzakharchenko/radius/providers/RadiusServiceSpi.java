package com.github.vzakharchenko.radius.providers;

import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

public class RadiusServiceSpi implements Spi {

    public static final String RADIUS_SPI = "radius-service-spi";

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return RADIUS_SPI;
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return IRadiusServiceProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return IRadiusServiceProviderFactory.class;
    }
}
