package com.github.vzakharchenko.radius.radius.handlers.protocols;

import com.github.vzakharchenko.radius.radius.handlers.session.KeycloakSessionUtils;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoGetter;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
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
    public boolean verifyProtocolPassword(String password) {
        String userPassword = accessRequest.getUserPassword();
        return
                userPassword.equals(password);
    }

    @Override
    public boolean verifyProtocolPassword() {
        UserModel userModel = KeycloakSessionUtils
                .getRadiusSessionInfo(session).getUserModel();
        if (session
                .userCredentialManager()
                .isValid(getRealm(),
                        userModel,
                        UserCredentialModel
                                .password(accessRequest.getUserPassword()),
                        UserCredentialModel
                                .kerberos(accessRequest.getUserPassword()))) {
            markActivePassword(accessRequest.getUserPassword());
            return true;
        }
        return false;
    }


}
