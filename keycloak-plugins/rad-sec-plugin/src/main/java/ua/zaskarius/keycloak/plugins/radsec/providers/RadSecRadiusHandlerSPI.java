package ua.zaskarius.keycloak.plugins.radsec.providers;

import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

public class RadSecRadiusHandlerSPI implements Spi {

    public static final String RAD_SEC_SPI = "radsec-radius-spi";

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return RAD_SEC_SPI;
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return IRadiusRadSecHandlerProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return IRadiusRadSecHandlerProviderFactory.class;
    }
}
