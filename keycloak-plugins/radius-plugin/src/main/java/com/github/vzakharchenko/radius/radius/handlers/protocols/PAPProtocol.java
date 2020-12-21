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

import java.util.Collection;
import java.util.Objects;

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
                userPassword.equals(password) ||
                        verifyPapPassword(password);
    }

    private boolean verifyProtocolPassword(UserCredentialManager userCredentialManager,
                                           UserModel userModel,
                                           CredentialInput credentialInput) {
        return userCredentialManager
                .isValid(getRealm(),
                        userModel,
                        credentialInput);
    }

    private boolean verifyPapPassword(String password) {
        UserModel userModel = Objects.requireNonNull(KeycloakSessionUtils
                .getRadiusSessionInfo(session)).getUserModel();
        UserCredentialManager userCredentialManager = session
                .userCredentialManager();
        if (
                verifyProtocolPassword(userCredentialManager, userModel, UserCredentialModel
                        .password(password)) ||
                        verifyProtocolPassword(userCredentialManager, userModel, UserCredentialModel
                                .kerberos(password))
        ) {
            markActivePassword(accessRequest.getUserPassword());
            return true;
        }
        return false;
    }

    @Override
    public boolean verifyPasswordWithoutOtp() {
        return verifyPapPassword(accessRequest.getUserPassword());
    }

    @Override
    public boolean verifyPasswordOtp() {
        Collection<String> passwordsWithOtp = getPasswordsWithOtp(accessRequest.getUserPassword());
        return passwordsWithOtp.stream().anyMatch(this::verifyProtocolPassword);
    }
}
