package com.github.vzakharchenko.radius.providers;

import org.keycloak.provider.Spi;

public class RadiusCOASpi implements Spi {

    public static final String RADIUS_SPI = "radius-coa-spi";

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return RADIUS_SPI;
    }

    @Override
    public Class<? extends IRadiusCOAProvider> getProviderClass() {
        return IRadiusCOAProvider.class;
    }

    @Override
    public Class<? extends IRadiusCOAProviderFactory> getProviderFactoryClass() {
        return IRadiusCOAProviderFactory.class;
    }
}
