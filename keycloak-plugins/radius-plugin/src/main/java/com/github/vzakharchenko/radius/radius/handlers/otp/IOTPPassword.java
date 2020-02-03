package com.github.vzakharchenko.radius.radius.handlers.otp;

import com.github.vzakharchenko.radius.models.OtpHolder;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.OTPPolicy;
import org.keycloak.models.credential.dto.OTPCredentialData;
import org.keycloak.models.credential.dto.OTPSecretData;

import java.util.Map;

public interface IOTPPassword {
    Map<String, OtpHolder> getOTPPasswords(OTPCredentialData credentialData,
                                           OTPPolicy policy,
                                           OTPSecretData secretData, CredentialModel credential);
}
