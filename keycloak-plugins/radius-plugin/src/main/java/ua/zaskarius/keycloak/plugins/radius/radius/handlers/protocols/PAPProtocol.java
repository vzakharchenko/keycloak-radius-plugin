package ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols;

import ua.zaskarius.keycloak.plugins.radius.models.RadiusUserInfo;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;

public class PAPProtocol extends AbstractAuthProtocol {


    public PAPProtocol(AccessRequest accessRequest,
                       KeycloakSession session) {
        super(accessRequest, session);
    }

    @Override
    protected void answer(RadiusPacket answer, RadiusUserInfo radiusUserInfo) {

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
