package com.github.vzakharchenko.radius.radius.handlers.protocols;

import com.github.vzakharchenko.radius.radius.handlers.session.KeycloakSessionUtils;
import com.github.vzakharchenko.radius.radius.handlers.session.PasswordData;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoGetter;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.credential.CredentialInput;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.SubjectCredentialManager;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;

import java.util.Collection;
import java.util.Objects;

public class PAPProtocol extends AbstractAuthProtocol {

    public PAPProtocol(AccessRequest accessRequest, KeycloakSession session) {
        super(accessRequest, session);
    }


    @Override
    public ProtocolType getType() {
        return ProtocolType.PAP;
    }

    @Override
    protected void answer(RadiusPacket answer, IRadiusUserInfoGetter radiusUserInfoGetter) {
        // do nothing
    }

    /**
     * This method behaves slightly differently to {@link CHAPProtocol} or
     * {@link MSCHAPV2Protocol}, as there is no plain text version of the stored password. The
     * list of possible passwords is composed of the possible OPT token codes, which are passed
     * here as validOtpTokenCode by {@link AbstractAuthProtocol}. This is compared with the
     * password entered and, if necessary, the remaining password is validated by the
     * {@link SubjectCredentialManager}.
     *
     * @param validOtpTokenCode A currently valid OTP token code.
     * @return true if otpTokenCode matches and<br>
     * a) supplied user password contains only the code <b>and</b> OtpWithoutPassword mode is
     * enabled<br>
     * b) the supplied user password contains the valid password suffixed by the otpTokenCode
     */
    @Override
    @SuppressWarnings("PMD.SimplifyBooleanReturns") // improves readability
    public boolean verifyProtocolPassword(String validOtpTokenCode) {
        if (StringUtils.isEmpty(validOtpTokenCode)) {
            return false;
        }
        String userPasswordWithOtp = accessRequest.getUserPassword();
        if (StringUtils.isEmpty(userPasswordWithOtp)) {
            return false;
        }
        String userPassword = StringUtils.removeEnd(userPasswordWithOtp, validOtpTokenCode);
        if (userPasswordWithOtp.equals(userPassword)) {
            return false; // -> fail for this password, wrong OTP token code
        } else if (supportOtpWithoutPassword() && userPassword.isEmpty()) {
            return true; // -> success, valid OTP code without password in OtpWithoutPassword mode
        } else {
            return verifyPapPassword(userPassword); // -> valid OTP code, check password
        }
    }

    private boolean verifyProtocolPassword(UserModel userModel, CredentialInput credentialInput) {
        return userModel.credentialManager().isValid(credentialInput);
    }

    private boolean verifyPapPassword(String password) {
        if (StringUtils.isEmpty(password)) {
            return false;
        }
        UserModel userModel =
                Objects.requireNonNull(
                        KeycloakSessionUtils.getRadiusSessionInfo(session)).getUserModel();
        if (verifyProtocolPassword(userModel, UserCredentialModel.password(password)) //
                || verifyProtocolPassword(userModel, UserCredentialModel.kerberos(password))) {
            markActivePassword(accessRequest.getUserPassword());
            return true;
        }
        return false;
    }

    @SuppressWarnings("PMD.SimplifyBooleanReturns") // improves readability
    private boolean verifyProtocolPasswordAgainstPlaintext(String password) {
        if (StringUtils.isEmpty(password)) {
            return false;
        }
        String userPassword = accessRequest.getUserPassword();
        if (StringUtils.isEmpty(userPassword)) {
            return false;
        }
        return userPassword.equals(password);
    }

    @Override
    public final boolean verifyPassword(PasswordData password) {
        if (password == null || StringUtils.isEmpty(password.getPassword())) {
            return false;
        }
        Collection<String> passwordsWithOtp = addOtpToPassword(password);
        String passwordOtp = passwordsWithOtp.stream()
                .filter(this::verifyProtocolPasswordAgainstPlaintext)
                .findFirst().orElse(null);
        if (!StringUtils.isEmpty(passwordOtp)) {
            KeycloakSessionUtils.getRadiusUserInfo(session).getBuilder()
                    .activePassword(passwordOtp);
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
        if (supportOtpWithoutPassword()) {
            // triggers OtpWithoutPassword branch in AbstractAuthProtocol.verifyPasswordWithOtp()
            return super.verifyPasswordOtp();
        } else {
            // expect OTP token code always prefixed by UserPassword
            // getPasswordsWithOtp() here returns the password if OTP suffix is valid or nothing
            Collection<String> passwordsWithValidOtp =
                    getPasswordsWithOtp(accessRequest.getUserPassword());
            return passwordsWithValidOtp.stream().anyMatch(this::verifyPapPassword);
        }
    }
}
