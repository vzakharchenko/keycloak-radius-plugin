package com.github.vzakharchenko.radius.providers;

import org.keycloak.provider.ProviderFactory;

public interface IRadiusDictionaryProviderFactory<T extends IRadiusDictionaryProvider>
        extends ProviderFactory<T> {
}
