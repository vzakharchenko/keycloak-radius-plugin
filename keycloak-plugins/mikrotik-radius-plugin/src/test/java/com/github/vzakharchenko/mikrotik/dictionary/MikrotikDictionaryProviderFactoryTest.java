package com.github.vzakharchenko.mikrotik.dictionary;

import com.github.vzakharchenko.mikrotik.MikrotikConstantUtils;
import org.testng.annotations.Test;
import org.tinyradius.dictionary.DictionaryParser;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;

import java.io.IOException;
import java.util.Objects;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class MikrotikDictionaryProviderFactoryTest extends AbstractRadiusTest {
    private MikrotikDictionaryProviderFactory dictionaryProviderFactory =
            new MikrotikDictionaryProviderFactory();

    @Test
    public void testMethods() throws IOException {
        Objects.equals(MikrotikConstantUtils.MIKROTIK_SERVICE_ATTRIBUTE, "mikrotik");
        dictionaryProviderFactory.close();
        dictionaryProviderFactory.init(null);
        dictionaryProviderFactory.postInit(null);
        assertNotNull(dictionaryProviderFactory.getRealmAttributes());
        assertNotNull(dictionaryProviderFactory.create(session));
        assertEquals(dictionaryProviderFactory.getId(), "Mikrotik-Dictionary");
        DictionaryParser dictionaryParser = DictionaryParser.newClasspathParser();
        realDictionary = dictionaryParser
                .parseDictionary("org/tinyradius/dictionary/default_dictionary");
        dictionaryProviderFactory.parseDictionary(realDictionary);
    }
}
