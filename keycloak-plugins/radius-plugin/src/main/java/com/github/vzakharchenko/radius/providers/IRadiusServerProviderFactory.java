package com.github.vzakharchenko.radius.providers;

import org.keycloak.provider.ProviderFactory;

public interface IRadiusServerProviderFactory<T extends IRadiusServerProvider>
        extends ProviderFactory<T> {


}
