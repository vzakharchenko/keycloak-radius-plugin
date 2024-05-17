package com.github.vzakharchenko.radius.proxy.client;

import org.jboss.logging.Logger;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.RadiusPacket;

public final class RadiusProxyClientHelper {
    private static final Logger LOGGER = Logger.getLogger(RadiusProxyClientHelper.class);
    private static IRadiusProxyClient radiusProxyClient = new RadiusProxyClient();

    private RadiusProxyClientHelper() {
    }

    public static RadiusPacket requestProxy(Dictionary dictionary,
                                            IProxyRequestHandler coaRequestHandler,
                                            IProxyExceptionHandler exceptionHandler) {
        try {
            return radiusProxyClient.requestProxy(dictionary, coaRequestHandler);
        } catch (Exception ex) {
            LOGGER.error("Client Communication Error ", ex);
            return exceptionHandler.onException(ex);
        }
    }

    public static void setRadiusProxyClient(IRadiusProxyClient radiusCoAClient) {
        radiusProxyClient = radiusCoAClient;
    }
}
