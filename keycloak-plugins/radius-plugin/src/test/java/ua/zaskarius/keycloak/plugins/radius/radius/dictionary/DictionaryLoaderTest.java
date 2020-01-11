package ua.zaskarius.keycloak.plugins.radius.radius.dictionary;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.dictionary.DictionaryParser;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusDictionaryProvider;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;

public class DictionaryLoaderTest extends AbstractRadiusTest {
    private IRadiusDictionaryProvider dictionaryProvider;

    @BeforeMethod
    public void beforeMethods() {
        HashSet<IRadiusDictionaryProvider> iRadiusDictionaryProviders = new HashSet<>();
        when(session
                .getAllProviders(IRadiusDictionaryProvider.class)).thenReturn(iRadiusDictionaryProviders);
        dictionaryProvider = getProvider(IRadiusDictionaryProvider.class);
        iRadiusDictionaryProviders.add(dictionaryProvider);
        when(dictionaryProvider.getDictionaryParser()).thenReturn(DictionaryParser
                .newClasspathParser());
        when(dictionaryProvider.getResources()).thenReturn(Arrays.asList("MS"));
        DictionaryLoader.setDictionaryLoader(new DictionaryLoader());
    }

    @Test
    public void testMethods() {

        Dictionary dictionary = DictionaryLoader.getInstance().loadDictionary(session);
        assertNotNull(dictionary);
        when(dictionaryProvider.getResources()).thenReturn(Collections.emptyList());
        dictionary = DictionaryLoader.getInstance().loadDictionary(session);
        assertNotNull(dictionary);
    }
}
