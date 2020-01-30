package com.github.vzakharchenko.radius.proxy.providers;

import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

public class RadiusProxyEndpointSpi implements Spi {

    public static final String RADIUS_SPI = "radius-proxy-endpoint-spi";

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
        return IRadiusProxyEndpointProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return IRadiusProxyEndpointProviderFactory.class;
    }
}
