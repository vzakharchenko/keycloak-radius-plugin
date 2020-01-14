package ua.zaskarius.keycloak.plugins.radius.radius.dictionary;

import com.google.common.annotations.VisibleForTesting;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.dictionary.MemoryDictionary;
import org.tinyradius.dictionary.WritableDictionary;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusDictionaryProvider;

import java.util.Set;

public class DictionaryLoader implements IDictionaryLoader {
    private static IDictionaryLoader loader = new DictionaryLoader();

    private final WritableDictionary dictionary = new MemoryDictionary();

    protected DictionaryLoader() {
    }

    @Override
    public Dictionary loadDictionary(KeycloakSession session) {
        Set<IRadiusDictionaryProvider> providers = session
                .getAllProviders(IRadiusDictionaryProvider.class);
        for (IRadiusDictionaryProvider provider : providers) {
            provider.parseDictionary(dictionary);
        }
        return dictionary;
    }

    @VisibleForTesting
    public static void setDictionaryLoader(IDictionaryLoader dictionaryLoader) {
        DictionaryLoader.loader = dictionaryLoader;
    }

    public static IDictionaryLoader getInstance() {
        return loader;
    }
}
