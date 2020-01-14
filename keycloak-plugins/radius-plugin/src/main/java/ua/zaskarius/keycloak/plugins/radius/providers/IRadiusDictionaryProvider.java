package ua.zaskarius.keycloak.plugins.radius.providers;

import org.keycloak.provider.Provider;
import org.tinyradius.dictionary.WritableDictionary;

import java.util.List;

public interface IRadiusDictionaryProvider extends Provider {

    List<String> getRealmAttributes();

    void parseDictionary(WritableDictionary dictionary);
}
