package com.github.vzakharchenko.radius.radius.handlers.attributes;

import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.RadiusPacket;

public interface KeycloakAttributes {
    KeycloakAttributes read();

    KeycloakAttributes ignoreUndefinedAttributes(Dictionary dictionary);

    void fillAnswer(RadiusPacket answer);
}
