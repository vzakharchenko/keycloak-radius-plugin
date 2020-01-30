package com.github.vzakharchenko.radius.radius.dictionary;

import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProvider;
import com.google.common.annotations.VisibleForTesting;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.dictionary.MemoryDictionary;
import org.tinyradius.dictionary.WritableDictionary;

import java.util.Set;

public final class DictionaryLoader {

    private static final DictionaryLoader INSTANCE = new DictionaryLoader();

    private WritableDictionary writableDictionary;

    private DictionaryLoader() {
    }

    public static DictionaryLoader getInstance() {
        return INSTANCE;
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

    @VisibleForTesting
    public void setWritableDictionary(WritableDictionary writableDictionary) {
        this.writableDictionary = writableDictionary;
    }
}
