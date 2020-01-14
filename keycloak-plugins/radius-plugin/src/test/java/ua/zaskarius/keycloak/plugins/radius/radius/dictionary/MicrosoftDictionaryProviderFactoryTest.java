package ua.zaskarius.keycloak.plugins.radius.radius.dictionary;

import org.testng.annotations.Test;
import org.tinyradius.dictionary.DictionaryParser;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.io.IOException;

import static org.testng.Assert.*;

public class MicrosoftDictionaryProviderFactoryTest extends AbstractRadiusTest {
    private MicrosoftDictionaryProviderFactory dictionaryProviderFactory =
            new MicrosoftDictionaryProviderFactory();
    @Test
    public void testMethods() throws IOException {
        dictionaryProviderFactory.close();
        dictionaryProviderFactory.init(null);
        dictionaryProviderFactory.postInit(null);
        assertNull(dictionaryProviderFactory.getRealmAttributes());
        assertNotNull(dictionaryProviderFactory.create(session));
        assertEquals(dictionaryProviderFactory.getId(),"Microsoft-Dictionary");

        DictionaryParser dictionaryParser = DictionaryParser.newClasspathParser();
        realDictionary = dictionaryParser
                .parseDictionary("org/tinyradius/dictionary/default_dictionary");
        dictionaryProviderFactory.parseDictionary(realDictionary);
    }
}
