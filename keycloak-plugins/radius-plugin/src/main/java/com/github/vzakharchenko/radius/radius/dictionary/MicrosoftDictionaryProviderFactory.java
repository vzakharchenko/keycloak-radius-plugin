package com.github.vzakharchenko.radius.radius.dictionary;

import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProvider;

public class MicrosoftDictionaryProviderFactory
        extends
        AbstractAttributesDictionaryProviderFactory
                <IRadiusDictionaryProvider> {

    @Override
    public String getId() {
        return "Microsoft-Dictionary";
    }

    @Override
    protected String getResourceName() {
        return "MS";
    }
}
