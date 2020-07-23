package com.github.vzakharchenko.chillispot.dictionary;

import com.github.vzakharchenko.radius.providers.AbstractRadiusDictionaryProvider;
import org.tinyradius.dictionary.WritableDictionary;

public class ChilliSpotDictionaryProviderFactory
        extends AbstractRadiusDictionaryProvider<ChilliSpotDictionaryProviderFactory> {

    public static final String CHILLI_SPOT_DICTIONARY = "ChilliSpot-Dictionary";
    public static final String CHILLI_SPOT = "ChilliSpot";
    public static final String CHILLI_SPOT_POST = "ChilliSpotPost";

    @Override
    public String getId() {
        return CHILLI_SPOT_DICTIONARY;
    }

    @Override
    protected String getResourceName() {
        return CHILLI_SPOT;
    }

    @Override
    public void parsePostDictionary(WritableDictionary dictionary) {
        parseDictionary(dictionary, CHILLI_SPOT_POST);
    }
}
