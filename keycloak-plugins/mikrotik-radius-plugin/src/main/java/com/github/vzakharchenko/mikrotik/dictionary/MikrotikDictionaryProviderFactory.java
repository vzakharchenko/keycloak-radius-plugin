package com.github.vzakharchenko.mikrotik.dictionary;

import com.github.vzakharchenko.radius.providers.AbstractRadiusDictionaryProvider;
import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProvider;
import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.util.Collections;
import java.util.List;

public class MikrotikDictionaryProviderFactory
        extends AbstractRadiusDictionaryProvider implements
        IRadiusDictionaryProviderFactory<IRadiusDictionaryProvider> {


    public static final String MIKROTIK = "mikrotik";

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
        return "Mikrotik-Dictionary";
    }

    @Override
    public List<String> getRealmAttributes() {
        return Collections.singletonList("Mikrotik-Realm");
    }


    @Override
    protected String getResourceName() {
        return MIKROTIK;
    }
}
