package ua.zaskarius.keycloak.plugins.radius.providers;

import org.keycloak.provider.ProviderFactory;

public interface IRadiusProviderFactory<T extends IRadiusConnectionProvider>
        extends ProviderFactory<T> {


}
