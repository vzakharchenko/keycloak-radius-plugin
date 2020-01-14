package ua.zaskarius.keycloak.plugins.radius.radius.dictionary;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusDictionaryProviderFactory;

import java.util.List;

public class DefaultDictionaryProviderFactory
        extends AbstractDictionaryProvider
        implements
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
    public List<String> getRealmAttributes() {
        return null;
    }

    @Override
    protected String getResource() {
        return "org/tinyradius/dictionary/default_dictionary";
    }
}
