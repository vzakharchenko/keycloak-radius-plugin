package com.github.vzakharchenko.radius.radius.handlers.protocols;

import com.github.vzakharchenko.radius.radius.handlers.session.KeycloakSessionUtils;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoGetter;
import org.keycloak.credential.CredentialInput;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserCredentialManager;
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

    private boolean verifyProtocolPassword(UserCredentialManager userCredentialManager,
                                           UserModel userModel,
                                           CredentialInput credentialInput) {
        return userCredentialManager
                .isValid(getRealm(),
                        userModel,
                        credentialInput);
    }

    @Override
    public boolean verifyProtocolPassword() {
        UserModel userModel = KeycloakSessionUtils
                .getRadiusSessionInfo(session).getUserModel();
        UserCredentialManager userCredentialManager = session
                .userCredentialManager();
        if (
                verifyProtocolPassword(userCredentialManager, userModel, UserCredentialModel
                        .password(accessRequest.getUserPassword())) ||
                        verifyProtocolPassword(userCredentialManager, userModel, UserCredentialModel
                                .kerberos(accessRequest.getUserPassword()))
        ) {
            markActivePassword(accessRequest.getUserPassword());
            return true;
        }
        return false;
    }


}
