package com.github.vzakharchenko.radius.radius.handlers.protocols;

import com.github.vzakharchenko.radius.radius.handlers.session.PasswordData;
import org.keycloak.models.RealmModel;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;

import java.net.InetSocketAddress;

public interface AuthProtocol {
    boolean verifyPassword(PasswordData password);

    boolean verifyPassword();

    RealmModel getRealm();

    boolean isValid(InetSocketAddress address);

    RadiusPacket prepareAnswer(RadiusPacket answer);

    ProtocolType getType();

    AccessRequest getAccessRequest();
}
