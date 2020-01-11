package ua.zaskarius.keycloak.plugins.radius.radius.dictionary;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.tinyradius.dictionary.DictionaryParser;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusDictionaryProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusDictionaryProviderFactory;

import java.util.Collections;
import java.util.List;

public class DefaultDictionaryProviderFactory
        implements IRadiusDictionaryProvider,
        IRadiusDictionaryProviderFactory<DefaultDictionaryProviderFactory> {


    @Override
    public DefaultDictionaryProviderFactory create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "Default-Dictionary";
    }

    @Override
    public DictionaryParser getDictionaryParser() {
        return DictionaryParser.newClasspathParser();
    }

    @Override
    public List<String> getResources() {
        return Collections.singletonList("org/tinyradius/dictionary/default_dictionary");
    }
}
