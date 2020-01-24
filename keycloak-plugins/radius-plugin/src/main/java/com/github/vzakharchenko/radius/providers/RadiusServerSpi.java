package com.github.vzakharchenko.radius.providers;

import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

public class RadiusServerSpi implements Spi {

    public static final String RADIUS_SPI = "radius-server-spi";

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
        return IRadiusServerProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return IRadiusServerProviderFactory.class;
    }
}
