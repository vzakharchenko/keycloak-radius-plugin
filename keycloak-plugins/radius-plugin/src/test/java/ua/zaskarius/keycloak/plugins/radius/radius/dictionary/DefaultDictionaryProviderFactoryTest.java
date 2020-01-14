package ua.zaskarius.keycloak.plugins.radius.radius.dictionary;

import org.testng.annotations.Test;
import org.tinyradius.dictionary.DictionaryParser;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.io.IOException;

import static org.testng.Assert.*;

public class DefaultDictionaryProviderFactoryTest extends AbstractRadiusTest {
    private DefaultDictionaryProviderFactory dictionaryProviderFactory =
            new DefaultDictionaryProviderFactory();

    @Test
    public void testMethods() throws IOException {
        dictionaryProviderFactory.close();
        dictionaryProviderFactory.init(null);
        dictionaryProviderFactory.postInit(null);
        assertNull(dictionaryProviderFactory.getRealmAttributes());
        assertNotNull(dictionaryProviderFactory.create(session));
        DictionaryParser dictionaryParser = DictionaryParser.newClasspathParser();
        realDictionary = dictionaryParser
                .parseDictionary("MS");
        assertEquals(dictionaryProviderFactory.getId(), "Default-Dictionary");
        dictionaryProviderFactory.parseDictionary(realDictionary);
    }
}
