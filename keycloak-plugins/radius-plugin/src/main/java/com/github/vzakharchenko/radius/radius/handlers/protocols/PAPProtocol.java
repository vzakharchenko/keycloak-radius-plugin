package com.github.vzakharchenko.radius.radius.handlers.protocols;

import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoGetter;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;

public class PAPProtocol extends AbstractAuthProtocol {


    public PAPProtocol(AccessRequest accessRequest,
                       KeycloakSession session) {
        super(accessRequest, session);
    }

    @Override
    protected void answer(RadiusPacket answer, IRadiusUserInfoGetter radiusUserInfoGetter) {

    }

    @Override
    public ProtocolType getType() {
        return ProtocolType.PAP;
    }

    @Override
    public boolean verifyPassword(String password) {
        return accessRequest.getUserPassword().equals(password);
    }
}
