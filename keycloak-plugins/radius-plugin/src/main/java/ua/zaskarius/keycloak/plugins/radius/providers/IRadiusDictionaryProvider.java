package ua.zaskarius.keycloak.plugins.radius.providers;

import org.keycloak.provider.Provider;
import org.tinyradius.dictionary.DictionaryParser;

import java.util.List;

public interface IRadiusDictionaryProvider extends Provider {
    DictionaryParser getDictionaryParser();
    List<String> getResources();
}
