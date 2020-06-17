package com.github.vzakharchenko.radius.providers;

import org.apache.commons.io.FileUtils;
import org.tinyradius.dictionary.DictionaryParser;
import org.tinyradius.dictionary.WritableDictionary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public abstract class AbstractRadiusDictionaryProvider implements IRadiusDictionaryProvider {

    protected abstract String getResourceName();

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
                .getResourceAsStream(getResourceName())) {
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
