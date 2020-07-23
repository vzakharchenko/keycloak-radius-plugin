package com.github.vzakharchenko.radius.radius.dictionary;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;
import org.tinyradius.dictionary.DictionaryParser;

import java.io.IOException;

import static org.testng.Assert.*;

public class ChilliSpotDictionaryProviderFactoryTest extends AbstractRadiusTest {
    private ChilliSpotDictionaryProviderFactory dictionaryProviderFactory =
            new ChilliSpotDictionaryProviderFactory();
    @Test
    public void testMethods() throws IOException {
        dictionaryProviderFactory.close();
        dictionaryProviderFactory.init(null);
        dictionaryProviderFactory.postInit(null);
        assertNull(dictionaryProviderFactory.getRealmAttributes());
        assertNotNull(dictionaryProviderFactory.create(session));
        assertEquals(dictionaryProviderFactory.getId(),"ChilliSpot-Dictionary");

        DictionaryParser dictionaryParser = DictionaryParser.newClasspathParser();
        realDictionary = dictionaryParser
                .parseDictionary("org/tinyradius/dictionary/default_dictionary");
        dictionaryProviderFactory.parseDictionary(realDictionary);
    }
}
