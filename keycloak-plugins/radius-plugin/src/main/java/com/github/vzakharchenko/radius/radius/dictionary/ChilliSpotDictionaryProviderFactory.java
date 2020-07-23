package com.github.vzakharchenko.radius.radius.dictionary;

import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProvider;
import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.util.List;

public class ChilliSpotDictionaryProviderFactory
        extends AbstractDictionaryProvider
        implements
        IRadiusDictionaryProviderFactory<IRadiusDictionaryProvider> {


    @Override
    public IRadiusDictionaryProvider create(KeycloakSession session) {
        return this;
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
    public String getId() {
        return "ChilliSpot-Dictionary";
    }

    @Override
    public List<String> getRealmAttributes() {
        return null;
    }

    @Override
    protected String getResource() {
        return "ChilliSpot";
    }
}
