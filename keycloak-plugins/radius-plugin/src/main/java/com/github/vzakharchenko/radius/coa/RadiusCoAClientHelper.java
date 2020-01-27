package com.github.vzakharchenko.radius.coa;

import org.tinyradius.dictionary.Dictionary;

public final class RadiusCoAClientHelper {

    private static IRadiusCoAClient radiusCoAClient = new RadiusCoAClient();

    private RadiusCoAClientHelper() {
    }

    public static void requestCoA(Dictionary dictionary,
                                  ICoaRequestHandler coaRequestHandler) {
        radiusCoAClient.requestCoA(dictionary, coaRequestHandler);
    }

    public static void setRadiusCoAClient(IRadiusCoAClient radiusCoAClient) {
        RadiusCoAClientHelper.radiusCoAClient = radiusCoAClient;
    }
}
