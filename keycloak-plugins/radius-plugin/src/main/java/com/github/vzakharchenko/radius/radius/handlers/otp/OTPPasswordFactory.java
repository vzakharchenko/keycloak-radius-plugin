package com.github.vzakharchenko.radius.radius.handlers.otp;

import com.github.vzakharchenko.radius.models.OtpHolder;
import com.github.vzakharchenko.radius.radius.handlers.session.KeycloakSessionUtils;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfo;
import org.keycloak.credential.*;
import org.keycloak.models.*;
import org.keycloak.models.credential.OTPCredentialModel;
import org.keycloak.models.credential.dto.OTPCredentialData;
import org.keycloak.models.credential.dto.OTPSecretData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.keycloak.models.credential.OTPCredentialModel.HOTP;
import static org.keycloak.models.credential.OTPCredentialModel.TOTP;

public class OTPPasswordFactory implements IOtpPasswordFactory {

    private final Map<String, IOTPPassword> otpPasswordFactories;

    public OTPPasswordFactory() {
        this.otpPasswordFactories = new HashMap<>();
        this.otpPasswordFactories.put(HOTP, new HotpPassword());
        this.otpPasswordFactories.put(TOTP, new TotpPassword());
    }

    private UserCredentialStore getCredentialStore(KeycloakSession session) {
        return session.userCredentialManager();
    }


    private Map<String, OtpHolder> getOTPPasswords(RealmModel realm,
                                                   CredentialModel credential) {
        Map<String, OtpHolder> otpHolderMap = new HashMap<>();
        OTPCredentialModel otpCredentialModel = OTPCredentialModel
                .createFromCredentialModel(credential);
        OTPSecretData secretData = otpCredentialModel.getOTPSecretData();
        OTPCredentialData credentialData = otpCredentialModel.getOTPCredentialData();
        OTPPolicy policy = realm.getOTPPolicy();
        IOTPPassword otpPassword = otpPasswordFactories.get(credentialData.getSubType());
        if (otpPassword != null) {
            otpHolderMap.putAll(otpPassword.getOTPPasswords(
                    credentialData, policy, secretData, credential));
        }
        return otpHolderMap;
    }

    private List<CredentialModel> filterCredentials(KeycloakSession session,
                                                    RealmModel realmModel,
                                                    UserModel userModel) {
        List<CredentialModel> credentials = getCredentialStore(session)
                .getStoredCredentialsByType(realmModel, userModel, OTPCredentialModel.TYPE);
        return credentials.stream().filter(credentialModel -> Objects
                .equals(OTPCredentialModel.createFromCredentialModel(credentialModel)
                        .getOTPCredentialData().getSubType(), realmModel
                        .getOTPPolicy().getType())).collect(Collectors.toList());
    }

    @Override
    public Map<String, OtpHolder> getOTPs(KeycloakSession session) {
        IRadiusUserInfo radiusSessionInfo = KeycloakSessionUtils
                .getRadiusSessionInfo(session);
        UserModel userModel = radiusSessionInfo.getUserModel();
        RealmModel realm = radiusSessionInfo.getRealmModel();
        List<CredentialModel> credentials = filterCredentials(session, realm, userModel);
        Map<String, OtpHolder> otpHolderMap = new HashMap<>();
        for (CredentialModel credential : credentials) {
            otpHolderMap.putAll(getOTPPasswords(realm, credential));
        }
        return otpHolderMap;
    }

    @Override
    public void validOTP(KeycloakSession session, String password, String credId, String type) {
        IRadiusUserInfo radiusSessionInfo = KeycloakSessionUtils
                .getRadiusSessionInfo(session);
        UserModel userModel = radiusSessionInfo.getUserModel();
        RealmModel realm = radiusSessionInfo.getRealmModel();
        OTPCredentialProvider provider = (OTPCredentialProvider) session
                .getProvider(CredentialProvider.class,
                        OTPCredentialProviderFactory.PROVIDER_ID);
        provider.isValid(realm, userModel, new UserCredentialModel(credId, type, password));
    }

}
