package ua.zaskarius.keycloak.plugins.radius.radius.dictionary;

import com.google.common.annotations.VisibleForTesting;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.dictionary.DictionaryParser;
import org.tinyradius.dictionary.MemoryDictionary;
import org.tinyradius.dictionary.WritableDictionary;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusDictionaryProvider;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class DictionaryLoader implements IDictionaryLoader {
    private static IDictionaryLoader dictionaryLoader = new DictionaryLoader();

    private WritableDictionary dictionary = new MemoryDictionary();

    protected DictionaryLoader() {
    }

    @Override
    public Dictionary loadDictionary(KeycloakSession session) {
        Set<IRadiusDictionaryProvider> providers = session
                .getAllProviders(IRadiusDictionaryProvider.class);
        for (IRadiusDictionaryProvider provider : providers) {
            List<String> resources = provider.getResources();
            DictionaryParser dictionaryParser = provider.getDictionaryParser();
            for (String resource : resources) {
                try {
                    dictionaryParser.parseDictionary(dictionary, resource);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return dictionary;
    }

    @VisibleForTesting
    public static void setDictionaryLoader(IDictionaryLoader dictionaryLoader) {
        DictionaryLoader.dictionaryLoader = dictionaryLoader;
    }

    public static IDictionaryLoader getInstance() {
        return dictionaryLoader;
    }
}
