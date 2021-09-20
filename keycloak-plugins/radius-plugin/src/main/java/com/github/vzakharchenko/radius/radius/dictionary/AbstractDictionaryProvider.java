package com.github.vzakharchenko.radius.radius.dictionary;

import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProvider;
import org.apache.commons.lang3.StringUtils;
import org.tinyradius.dictionary.DictionaryParser;
import org.tinyradius.dictionary.WritableDictionary;

import java.io.IOException;

public abstract class AbstractDictionaryProvider implements
        IRadiusDictionaryProvider {

    protected abstract String getResourceName();

    protected DictionaryParser getParser() {
        return DictionaryParser.newClasspathParser();
    }

    @Override
    public void parseDictionary(WritableDictionary dictionary) {
        DictionaryParser dictionaryParser = getParser();
        try {
            String resourceName = getResourceName();
            if (StringUtils.isNoneEmpty(resourceName)) {
                dictionaryParser.parseDictionary(dictionary, resourceName);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void parsePostDictionary(WritableDictionary dictionary) {

    }
}
