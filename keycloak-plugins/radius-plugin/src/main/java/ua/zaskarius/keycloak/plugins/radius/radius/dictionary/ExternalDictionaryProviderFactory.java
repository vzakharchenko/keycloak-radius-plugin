package ua.zaskarius.keycloak.plugins.radius.radius.dictionary;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.tinyradius.dictionary.DictionaryParser;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusDictionaryProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusDictionaryProviderFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExternalDictionaryProviderFactory
        implements IRadiusDictionaryProvider,
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
        return "External-Dictionary";
    }

    @Override
    public DictionaryParser getDictionaryParser() {
        return DictionaryParser.newFileParser();
    }

    @Override
    public List<String> getResources() {
        List<String> resources = new ArrayList<>();
        File directory = new File("config", "dictionary");
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.exists() && !file.isDirectory()) {
                        resources.add(file.getAbsolutePath());
                    }
                }
            }
        }
        return resources;
    }
}
