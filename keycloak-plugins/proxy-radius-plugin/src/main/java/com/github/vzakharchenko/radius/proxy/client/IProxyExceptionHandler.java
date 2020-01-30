package com.github.vzakharchenko.radius.proxy.client;

import org.tinyradius.packet.RadiusPacket;

public interface IProxyExceptionHandler {
    RadiusPacket onException(Exception ex);
}
