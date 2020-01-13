package ua.zaskarius.keycloak.plugins.radius.providers;

import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

public class AuthRadiusHandlerSPI implements Spi {

    public static final String AUTH_RADIUS_SPI = "auth-radius-spi";

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return AUTH_RADIUS_SPI;
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return IRadiusAuthHandlerProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return IRadiusAuthHandlerProviderFactory.class;
    }
}
