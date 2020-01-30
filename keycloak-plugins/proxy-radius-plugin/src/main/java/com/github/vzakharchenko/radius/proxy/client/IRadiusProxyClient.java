package com.github.vzakharchenko.radius.proxy.client;

import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.RadiusPacket;

public interface IRadiusProxyClient {
    RadiusPacket requestProxy(Dictionary dictionary,
                              IProxyRequestHandler coaRequestHandler);
}
