package ua.zaskarius.keycloak.plugins.radius.radius.dictionary;

import org.tinyradius.dictionary.DictionaryParser;
import org.tinyradius.dictionary.WritableDictionary;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusDictionaryProvider;

import java.io.IOException;

public abstract class AbstractDictionaryProvider implements
        IRadiusDictionaryProvider {

    protected abstract String getResource();

    @Override
    public void parseDictionary(WritableDictionary dictionary) {
        DictionaryParser dictionaryParser = DictionaryParser.newClasspathParser();
        try {
            dictionaryParser.parseDictionary(dictionary, getResource());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
