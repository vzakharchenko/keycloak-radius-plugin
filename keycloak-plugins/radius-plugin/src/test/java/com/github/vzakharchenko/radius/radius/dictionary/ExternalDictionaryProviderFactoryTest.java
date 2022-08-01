package com.github.vzakharchenko.radius.radius.dictionary;

import com.github.vzakharchenko.radius.models.RadiusServerSettings;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import com.github.vzakharchenko.radius.test.ModelBuilder;
import org.testng.annotations.Test;
import org.tinyradius.attribute.AttributeType;
import org.tinyradius.dictionary.DictionaryParser;

import java.io.IOException;

import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class ExternalDictionaryProviderFactoryTest extends AbstractRadiusTest {
    private final ExternalDictionaryProviderFactory dictionaryProviderFactory =
            new ExternalDictionaryProviderFactory();

    @Test
    public void testExternalDictionary() throws IOException {
        RadiusServerSettings radiusServerSettings = ModelBuilder.createRadiusServerSettings();
        radiusServerSettings.setExternalDictionary("./src/test/resources/Fortinet");
        when(configuration.getRadiusSettings())
                .thenReturn(radiusServerSettings);
        dictionaryProviderFactory.close();
        dictionaryProviderFactory.init(null);
        dictionaryProviderFactory.postInit(null);
        assertNotNull(dictionaryProviderFactory.getRealmAttributes());
        assertTrue(dictionaryProviderFactory.getRealmAttributes().isEmpty());
        assertNotNull(dictionaryProviderFactory.create(session));
        DictionaryParser dictionaryParser = DictionaryParser.newClasspathParser();
        realDictionary = dictionaryParser
                .parseDictionary("org/tinyradius/dictionary/default_dictionary");
        assertEquals(dictionaryProviderFactory.getId(), "External-Dictionary");
        dictionaryProviderFactory.parseDictionary(realDictionary);
        AttributeType attributeTypeByCode = realDictionary.getAttributeTypeByCode(12356, 1);
        assertNotNull(attributeTypeByCode);
        assertEquals(attributeTypeByCode.toString(), "1/Fortinet-Group-Name: string (vendor 12356)");
    }

    @Test
    public void testMethodsNull() throws IOException {
        RadiusServerSettings radiusServerSettings = ModelBuilder.createRadiusServerSettings();
        radiusServerSettings.setExternalDictionary(null);
        when(configuration.getRadiusSettings())
                .thenReturn(radiusServerSettings);
        DictionaryParser dictionaryParser = DictionaryParser.newClasspathParser();
        realDictionary = dictionaryParser
                .parseDictionary("org/tinyradius/dictionary/default_dictionary");
        assertEquals(dictionaryProviderFactory.getId(), "External-Dictionary");
        dictionaryProviderFactory.parseDictionary(realDictionary);
        AttributeType attributeTypeByCode = realDictionary.getAttributeTypeByCode(391, 1);
        assertNull(attributeTypeByCode);
    }

    @Test
    public void testMethodsEmpty() throws IOException {
        RadiusServerSettings radiusServerSettings = ModelBuilder.createRadiusServerSettings();
        radiusServerSettings.setExternalDictionary("");
        when(configuration.getRadiusSettings())
                .thenReturn(radiusServerSettings);
        DictionaryParser dictionaryParser = DictionaryParser.newClasspathParser();
        realDictionary = dictionaryParser
                .parseDictionary("org/tinyradius/dictionary/default_dictionary");
        assertEquals(dictionaryProviderFactory.getId(), "External-Dictionary");
        dictionaryProviderFactory.parseDictionary(realDictionary);
        AttributeType attributeTypeByCode = realDictionary.getAttributeTypeByCode(391, 1);
        assertNull(attributeTypeByCode);
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "External dictionary test_dictionary does not exists")
    public void testMethodsNotExists() throws IOException {
        RadiusServerSettings radiusServerSettings = ModelBuilder.createRadiusServerSettings();
        radiusServerSettings.setExternalDictionary("test_dictionary");
        when(configuration.getRadiusSettings())
                .thenReturn(radiusServerSettings);
        DictionaryParser dictionaryParser = DictionaryParser.newClasspathParser();
        realDictionary = dictionaryParser
                .parseDictionary("org/tinyradius/dictionary/default_dictionary");
        assertEquals(dictionaryProviderFactory.getId(), "External-Dictionary");
        dictionaryProviderFactory.parseDictionary(realDictionary);
    }
}
