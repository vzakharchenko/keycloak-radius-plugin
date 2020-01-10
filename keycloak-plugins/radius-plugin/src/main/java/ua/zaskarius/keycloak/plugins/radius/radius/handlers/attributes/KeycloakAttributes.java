package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;

public interface KeycloakAttributes {
    KeycloakAttributes read();

    KeycloakAttributes ignoreUndefinedAttributes(Dictionary dictionary);

    KeycloakAttributes filter(AccessRequest accessRequest);

    void fillAnswer(RadiusPacket answer);
}
