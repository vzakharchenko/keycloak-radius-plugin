package com.github.vzakharchenko.radius.radius.handlers.protocols;

import com.github.vzakharchenko.radius.radius.handlers.session.KeycloakSessionUtils;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoGetter;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.credential.CredentialInput;
import org.keycloak.models.KeycloakSession;
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

    private boolean verifyProtocolPassword(UserModel userModel,
                                           CredentialInput credentialInput) {
        return userModel.credentialManager()
                .isValid(
                        credentialInput);
    }

    private boolean verifyPapPassword(String password) {
        if (StringUtils.isEmpty(password)) {
            return false;
        }
        UserModel userModel = Objects.requireNonNull(KeycloakSessionUtils
                .getRadiusSessionInfo(session)).getUserModel();
        if (
                verifyProtocolPassword(userModel, UserCredentialModel
                        .password(password)) ||
                        verifyProtocolPassword(userModel, UserCredentialModel
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

    @Override
    protected boolean supportOtpWithoutPassword() {
        return false;
    }
}
