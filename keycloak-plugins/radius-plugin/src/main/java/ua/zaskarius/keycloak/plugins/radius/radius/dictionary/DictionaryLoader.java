package ua.zaskarius.keycloak.plugins.radius.radius.dictionary;

import org.keycloak.models.KeycloakSession;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.dictionary.MemoryDictionary;
import org.tinyradius.dictionary.WritableDictionary;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusDictionaryProvider;

import java.util.Set;

public final class DictionaryLoader {

    private static final DictionaryLoader INSTANCE = new DictionaryLoader();

    private WritableDictionary writableDictionary;

    private DictionaryLoader() {
    }

    public Dictionary loadDictionary(KeycloakSession session) {
        if (writableDictionary == null) {
            writableDictionary = new MemoryDictionary();
            Set<IRadiusDictionaryProvider> providers = session
                    .getAllProviders(IRadiusDictionaryProvider.class);
            for (IRadiusDictionaryProvider provider : providers) {
                provider.parseDictionary(writableDictionary);
            }
        }
        return writableDictionary;
    }

    public static DictionaryLoader getInstance() {
        return INSTANCE;
    }
}
