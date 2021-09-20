package com.github.vzakharchenko.radius.radius.dictionary;

import com.github.vzakharchenko.radius.configuration.RadiusConfigHelper;
import org.apache.commons.lang3.StringUtils;
import org.tinyradius.dictionary.DictionaryParser;

import java.io.File;

public class ExternalDictionaryProviderFactory
        extends AbstractAttributesDictionaryProviderFactory
        <ExternalDictionaryProviderFactory> {
    @Override
    public String getId() {
        return "External-Dictionary";
    }

    @Override
    protected DictionaryParser getParser() {
        return DictionaryParser.newFileParser();
    }

    @Override
    protected String getResourceName() {
        String externalDictionary = RadiusConfigHelper
                .getConfig().getRadiusSettings().getExternalDictionary();
        if (StringUtils.isEmpty(externalDictionary)) {
            return null;
        } else {
            File file = new File(externalDictionary);
            if (!file.exists()) {
                throw new IllegalStateException("External dictionary " +
                        externalDictionary + " does not exists");
            }
            return externalDictionary;
        }
    }
}
