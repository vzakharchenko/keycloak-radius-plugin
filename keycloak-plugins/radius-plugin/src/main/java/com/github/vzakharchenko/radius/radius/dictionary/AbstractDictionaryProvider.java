package com.github.vzakharchenko.radius.radius.dictionary;

import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProvider;
import org.tinyradius.dictionary.DictionaryParser;
import org.tinyradius.dictionary.WritableDictionary;

import java.io.IOException;

public abstract class AbstractDictionaryProvider implements
        IRadiusDictionaryProvider {

    protected abstract String getResourceName();

    @Override
    public void parseDictionary(WritableDictionary dictionary) {
        DictionaryParser dictionaryParser = DictionaryParser.newClasspathParser();
        try {
            dictionaryParser.parseDictionary(dictionary, getResourceName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void parsePostDictionary(WritableDictionary dictionary) {

    }
}
