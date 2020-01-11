package ua.zaskarius.keycloak.plugins.radius.providers;

import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

public class RadiusDictionarySpi implements Spi {

    public static final String RADIUS_DICTIONARY = "radius-dictionary";

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return RADIUS_DICTIONARY;
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return IRadiusDictionaryProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return IRadiusDictionaryProviderFactory.class;
    }
}
