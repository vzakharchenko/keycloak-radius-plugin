package ua.in.zaskarius.keycloak.mikrotik.dictionary;

import org.apache.commons.io.FileUtils;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.tinyradius.dictionary.DictionaryParser;
import org.tinyradius.dictionary.WritableDictionary;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusDictionaryProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusDictionaryProviderFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MikrotikDictionaryProviderFactory
        implements IRadiusDictionaryProvider,
        IRadiusDictionaryProviderFactory<IRadiusDictionaryProvider> {


    public static final String MIKROTIK = "mikrotik";

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
        return "Mikrotik-Dictionary";
    }

    @Override
    public List<String> getRealmAttributes() {
        return Collections.singletonList("Mikrotik-Realm");
    }

    @Override
    public void parseDictionary(WritableDictionary dictionary) {
        DictionaryParser dictionaryParser = DictionaryParser.newFileParser();
        try {
            InputStream mikrotik = getClass().getClassLoader()
                    .getResourceAsStream(MIKROTIK);
            if (mikrotik == null) {
                throw new IllegalStateException("resource \"" + MIKROTIK + "\" does not exists");
            }
            File file = new File(System.getProperty("java.io.tmpdir"),
                    UUID.randomUUID().toString());
            try {
                FileUtils.copyInputStreamToFile(mikrotik, file);
                dictionaryParser.parseDictionary(dictionary,
                        file.getAbsolutePath());
            } finally {
                FileUtils.deleteQuietly(file);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
