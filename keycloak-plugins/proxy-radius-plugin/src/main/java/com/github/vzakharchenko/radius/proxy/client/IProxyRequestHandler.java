package com.github.vzakharchenko.radius.proxy.client;

import org.tinyradius.client.RadiusClient;
import org.tinyradius.packet.RadiusPacket;

public interface IProxyRequestHandler {
    RadiusPacket call(RadiusClient radiusClient);
}
