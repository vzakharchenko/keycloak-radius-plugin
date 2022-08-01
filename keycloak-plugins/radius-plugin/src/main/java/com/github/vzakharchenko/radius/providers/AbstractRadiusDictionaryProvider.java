package com.github.vzakharchenko.radius.providers;

import com.github.vzakharchenko.radius.radius.dictionary.AbstractAttributesDictionaryProviderFactory;
import org.apache.commons.io.FileUtils;
import org.tinyradius.dictionary.DictionaryParser;
import org.tinyradius.dictionary.WritableDictionary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public abstract class AbstractRadiusDictionaryProvider<T extends IRadiusDictionaryProvider>
        extends AbstractAttributesDictionaryProviderFactory<T> {

    public void parseDictionary(DictionaryParser dictionaryParser,
                                InputStream inputStream,
                                WritableDictionary dictionary) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir"),
                UUID.randomUUID().toString());
        try {
            FileUtils.copyInputStreamToFile(inputStream, file);
            dictionaryParser.parseDictionary(dictionary,
                    file.getAbsolutePath());
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    public void parseDictionary(DictionaryParser dictionaryParser,
                                WritableDictionary dictionary,
                                String resourceName) throws IOException {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(resourceName)) {
            parseDictionary(dictionaryParser, inputStream, dictionary);
        }
    }

    public void parseDictionary(WritableDictionary dictionary, String resourceName) {
        DictionaryParser dictionaryParser = DictionaryParser.newFileParser();
        try {
            parseDictionary(dictionaryParser, dictionary, resourceName);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void parseDictionary(WritableDictionary dictionary) {
        parseDictionary(dictionary, getResourceName());
    }
}
