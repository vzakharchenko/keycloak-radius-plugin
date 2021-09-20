package com.github.vzakharchenko.radius.radius.dictionary;

import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProvider;
import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.util.Collections;
import java.util.List;

public abstract class AbstractAttributesDictionaryProviderFactory
        <T extends IRadiusDictionaryProvider>
        extends AbstractDictionaryProvider
        implements
        IRadiusDictionaryProviderFactory<T> {


    @Override
    public T create(KeycloakSession session) {
        return (T) this;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public List<String> getRealmAttributes() {
        return Collections.EMPTY_LIST;
    }
}
