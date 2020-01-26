package com.github.vzakharchenko.radius.providers;

import org.keycloak.provider.ProviderFactory;

public interface IRadiusCOAProviderFactory<T extends IRadiusCOAProvider>
        extends ProviderFactory<T> {
}
