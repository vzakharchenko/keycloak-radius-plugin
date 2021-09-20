package com.github.vzakharchenko.radius.radius.dictionary;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;
import org.tinyradius.dictionary.DictionaryParser;

import java.io.IOException;

import static org.testng.Assert.*;

public class DefaultDictionaryProviderFactoryTest extends AbstractRadiusTest {
    private final DefaultDictionaryProviderFactory dictionaryProviderFactory =
            new DefaultDictionaryProviderFactory();

    @Test
    public void testMethods() throws IOException {
        dictionaryProviderFactory.close();
        dictionaryProviderFactory.init(null);
        dictionaryProviderFactory.postInit(null);
        assertNotNull(dictionaryProviderFactory.getRealmAttributes());
        assertTrue(dictionaryProviderFactory.getRealmAttributes().isEmpty());
        assertNotNull(dictionaryProviderFactory.create(session));
        DictionaryParser dictionaryParser = DictionaryParser.newClasspathParser();
        realDictionary = dictionaryParser
                .parseDictionary("MS");
        assertEquals(dictionaryProviderFactory.getId(), "Default-Dictionary");
        dictionaryProviderFactory.parseDictionary(realDictionary);
    }
}
