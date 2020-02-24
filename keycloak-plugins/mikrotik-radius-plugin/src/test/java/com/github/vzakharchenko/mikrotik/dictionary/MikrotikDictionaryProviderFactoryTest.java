package com.github.vzakharchenko.mikrotik.dictionary;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.dictionary.DictionaryParser;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class MikrotikDictionaryProviderFactoryTest extends AbstractRadiusTest {
    public static final String TARGET_MIKROTIK1 = "target/test-classes/mikrotik";
    public static final String TARGET_MIKROTIK2 = "target/target/mikrotik";
    private MikrotikDictionaryProviderFactory dictionaryProviderFactory =
            new MikrotikDictionaryProviderFactory();

    @BeforeMethod
    @AfterMethod
    public void beforeMethods() throws IOException {
        FileUtils.deleteQuietly(new File(TARGET_MIKROTIK1));
        DictionaryParser dictionaryParser = DictionaryParser.newClasspathParser();
        realDictionary = dictionaryParser
                .parseDictionary("org/tinyradius/dictionary/default_dictionary");
    }

    @Test
    public void testMethods() {
        dictionaryProviderFactory.close();
        dictionaryProviderFactory.init(null);
        dictionaryProviderFactory.postInit(null);
        assertNotNull(dictionaryProviderFactory.getRealmAttributes());
        assertNotNull(dictionaryProviderFactory.create(session));
        assertEquals(dictionaryProviderFactory.getId(), "Mikrotik-Dictionary");

        dictionaryProviderFactory.parseDictionary(realDictionary);
    }

    @Test(expectedExceptions = Exception.class)
    public void testError() throws IOException {
        File file = new File(TARGET_MIKROTIK1);
        file.createNewFile();
        FileUtils.write(file, "test", true);
        dictionaryProviderFactory.parseDictionary(realDictionary);
    }
}
