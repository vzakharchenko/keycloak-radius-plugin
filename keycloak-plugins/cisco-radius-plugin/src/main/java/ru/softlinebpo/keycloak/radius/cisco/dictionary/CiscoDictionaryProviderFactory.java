package ru.softlinebpo.keycloak.radius.cisco.dictionary;

import com.github.vzakharchenko.radius.providers.AbstractRadiusDictionaryProvider;

public class CiscoDictionaryProviderFactory
        extends AbstractRadiusDictionaryProvider<CiscoDictionaryProviderFactory> {

    public static final String DICTIONARY = "dictionary.cisco";
    public static final String CISCO_DICTIONARY = "Cisco-Dictionary";

    @Override
    public String getId() {
        return CISCO_DICTIONARY;
    }

    @Override
    protected String getResourceName() {
        return DICTIONARY;
    }
}
