package com.github.vzakharchenko.radius.radius.dictionary;

public class DefaultDictionaryProviderFactory
        extends AbstractAttributesDictionaryProviderFactory
        <DefaultDictionaryProviderFactory> {
    @Override
    public String getId() {
        return "Default-Dictionary";
    }

    @Override
    protected String getResourceName() {
        return "org/tinyradius/dictionary/default_dictionary";
    }
}
