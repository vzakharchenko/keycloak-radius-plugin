package ua.zaskarius.keycloak.plugins.radius.radius.dictionary;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusDictionaryProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusDictionaryProviderFactory;

import java.util.List;

public class MicrosoftDictionaryProviderFactory
        extends AbstractDictionaryProvider
        implements
        IRadiusDictionaryProviderFactory<IRadiusDictionaryProvider> {


    @Override
    public IRadiusDictionaryProvider create(KeycloakSession session) {
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
        return "Microsoft-Dictionary";
    }

    @Override
    public List<String> getRealmAttributes() {
        return null;
    }

    @Override
    protected String getResource() {
        return "MS";
    }
}
