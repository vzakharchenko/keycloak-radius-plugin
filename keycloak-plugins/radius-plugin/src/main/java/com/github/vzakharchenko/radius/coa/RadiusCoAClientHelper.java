package com.github.vzakharchenko.radius.coa;

import org.jboss.logging.Logger;
import org.tinyradius.dictionary.Dictionary;

public final class RadiusCoAClientHelper {
    private static final Logger LOGGER = Logger.getLogger(RadiusCoAClientHelper.class);
    private static IRadiusCoAClient radiusCoAClient = new RadiusCoAClient();

    private RadiusCoAClientHelper() {
    }

    public static void requestCoA(Dictionary dictionary,
                                  ICoaRequestHandler coaRequestHandler,
                                  ICoAExceptionHandler exceptionHandler) {
        try {
            radiusCoAClient.requestCoA(dictionary, coaRequestHandler);
        } catch (Exception ex) {
            LOGGER.error("Client Communication Error ", ex);
            if (exceptionHandler != null) {
                exceptionHandler.onException(ex);
            }
        }
    }

    public static void setRadiusCoAClient(IRadiusCoAClient radiusCoAClient) {
        RadiusCoAClientHelper.radiusCoAClient = radiusCoAClient;
    }
}
