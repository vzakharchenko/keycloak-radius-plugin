package ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols;

import org.keycloak.models.KeycloakSession;
import org.tinyradius.packet.AccessRequest;

import static org.tinyradius.packet.AccessRequest.*;

public final class RadiusAuthProtocolFactory implements AuthProtocolFactory {

    private static AuthProtocolFactory instance = new RadiusAuthProtocolFactory();

    private RadiusAuthProtocolFactory() {
    }

    public static AuthProtocolFactory getInstance() {
        return instance;
    }

    @Override
    public AuthProtocol create(AccessRequest request,
                               KeycloakSession threadSession) {
        switch (request.getAuthProtocol()) {
            case AUTH_CHAP:
                return new CHAPProtocol(request, threadSession);
            case AUTH_MS_CHAP_V2:
                return new MSCHAPV2Protocol(request, threadSession);
            case AUTH_EAP:
                throw new UnsupportedOperationException(
                        AUTH_EAP + " verification not supported yet");
            case AUTH_PAP:
            default:
                return new PAPProtocol(request, threadSession);

        }
    }

    public static void setInstance(AuthProtocolFactory instance) {
        RadiusAuthProtocolFactory.instance = instance;
    }
}
