package com.github.vzakharchenko.mikrotik.dictionary;

import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProvider;
import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProviderFactory;
import org.apache.commons.io.FileUtils;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.tinyradius.dictionary.DictionaryParser;
import org.tinyradius.dictionary.WritableDictionary;

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

    public void parseDictionary(DictionaryParser dictionaryParser,
                                InputStream mikrotik,
                                WritableDictionary dictionary) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir"),
                UUID.randomUUID().toString());
        try {
            FileUtils.copyInputStreamToFile(mikrotik, file);
            dictionaryParser.parseDictionary(dictionary,
                    file.getAbsolutePath());
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    public void parseDictionary(DictionaryParser dictionaryParser,
                                WritableDictionary dictionary) throws IOException {
        try (InputStream mikrotik = getClass().getClassLoader()
                .getResourceAsStream(MIKROTIK)) {
            parseDictionary(dictionaryParser, mikrotik, dictionary);
        }
    }

    @Override
    public void parseDictionary(WritableDictionary dictionary) {
        DictionaryParser dictionaryParser = DictionaryParser.newFileParser();
        try {
            parseDictionary(dictionaryParser, dictionary);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
