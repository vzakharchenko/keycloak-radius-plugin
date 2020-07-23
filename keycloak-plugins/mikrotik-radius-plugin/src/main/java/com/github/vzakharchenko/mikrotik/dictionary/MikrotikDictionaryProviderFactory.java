package com.github.vzakharchenko.mikrotik.dictionary;

import com.github.vzakharchenko.radius.providers.AbstractRadiusDictionaryProvider;

import java.util.Collections;
import java.util.List;

public class MikrotikDictionaryProviderFactory
        extends AbstractRadiusDictionaryProvider<MikrotikDictionaryProviderFactory> {

    public static final String MIKROTIK = "mikrotik";

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
