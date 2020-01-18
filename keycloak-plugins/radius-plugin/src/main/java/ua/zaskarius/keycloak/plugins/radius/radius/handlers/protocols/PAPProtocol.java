package ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols;

import org.keycloak.models.KeycloakSession;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import ua.zaskarius.keycloak.plugins.radius.radius.holder.IRadiusUserInfoGetter;

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
