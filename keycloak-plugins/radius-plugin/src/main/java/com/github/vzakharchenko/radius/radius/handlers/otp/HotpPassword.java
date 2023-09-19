package com.github.vzakharchenko.radius.radius.handlers.otp;

import com.github.vzakharchenko.radius.models.OtpHolder;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.OTPPolicy;
import org.keycloak.models.credential.dto.OTPCredentialData;
import org.keycloak.models.credential.dto.OTPSecretData;
import org.keycloak.models.utils.HmacOTP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotpPassword implements IOTPPassword {


    public List<String> getHOTPs(HmacOTP hmacOTP, String key,
                                 int counter,
                                 int lookAheadWindow) {
        List<String> passwords = new ArrayList<>(lookAheadWindow);
        for (int newCounter = counter; newCounter <= counter + lookAheadWindow; newCounter++) {
            String candidate = hmacOTP.generateHOTP(key, newCounter);
            passwords.add(candidate);
        }
        return passwords;
    }

    @Override
    public Map<String, OtpHolder> getOTPPasswords(OTPCredentialData credentialData,
                                                  OTPPolicy policy,
                                                  OTPSecretData secretData,
                                                  CredentialModel credential) {
        Map<String, OtpHolder> otpHolderMap = new HashMap<>();
        HmacOTP validator = new HmacOTP(credentialData.getDigits(),
                credentialData.getAlgorithm(), policy.getLookAheadWindow());
        List<String> hotPs = getHOTPs(validator, secretData.getValue(),
                credentialData.getCounter(), policy.getLookAheadWindow());
        otpHolderMap.put(credential.getId(),
                new OtpHolder(credentialData.getSubType(), credential, hotPs));
        return otpHolderMap;
    }
}
