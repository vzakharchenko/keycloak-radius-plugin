package com.github.vzakharchenko.radius.providers;

import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

public class AttributeSpi implements Spi {

    public static final String RADIUS_ATTRIBUTES_SPI = "radius-attributes-spi";

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return RADIUS_ATTRIBUTES_SPI;
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return IRadiusAttributeProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return IRadiusAttributesProviderFactory.class;
    }
}
