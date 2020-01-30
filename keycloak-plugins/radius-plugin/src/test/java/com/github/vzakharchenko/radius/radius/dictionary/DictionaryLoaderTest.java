package com.github.vzakharchenko.radius.radius.dictionary;

import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProvider;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.dictionary.Dictionary;

import java.util.HashSet;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;

public class DictionaryLoaderTest extends AbstractRadiusTest {
    @Mock
    private IRadiusDictionaryProvider dictionaryProvider;

    @BeforeMethod
    public void beforeMethods() {
        DictionaryLoader.getInstance().setWritableDictionary(null);
        HashSet<IRadiusDictionaryProvider> iRadiusDictionaryProviders = new HashSet<>();
        when(session
                .getAllProviders(IRadiusDictionaryProvider.class))
                .thenReturn(iRadiusDictionaryProviders);
        dictionaryProvider = getProvider(IRadiusDictionaryProvider.class);
        iRadiusDictionaryProviders.add(dictionaryProvider);
    }

    @Test
    public void testMethods() {

        Dictionary dictionary = DictionaryLoader.getInstance().loadDictionary(session);
        assertNotNull(dictionary);
        dictionary = DictionaryLoader.getInstance().loadDictionary(session);
        assertNotNull(dictionary);
    }
}
