package com.github.vzakharchenko.radius.radius.handlers.otp;

import com.github.vzakharchenko.radius.models.OtpHolder;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.OTPPolicy;
import org.keycloak.models.credential.dto.OTPCredentialData;
import org.keycloak.models.credential.dto.OTPSecretData;
import org.keycloak.models.utils.TimeBasedOTP;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class TotpPassword implements IOTPPassword {


    public List<String> getTOTPs(TimeBasedOTP timeBasedOTP,
                                 OTPCredentialData credentialData,
                                 byte[] secret, int lookAheadWindow) {
        List<String> passwords = new ArrayList<>(lookAheadWindow);
        long currentInterval = new OtpClock(credentialData.getPeriod()).getCurrentInterval();
        for (int i = lookAheadWindow; i >= 0; --i) {
            StringBuilder steps = new StringBuilder(Long
                    .toHexString(currentInterval - i).toUpperCase(Locale.US));
            while (steps.length() < 16) {
                steps.insert(0, "0");
            }
            String candidate = timeBasedOTP.generateOTP(secret, steps.toString(),
                    credentialData.getDigits(), credentialData.getAlgorithm());
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
        TimeBasedOTP validator = new TimeBasedOTP(credentialData.getAlgorithm(),
                credentialData.getDigits(), credentialData.getPeriod(),
                policy.getLookAheadWindow());
        List<String> totPs = getTOTPs(validator, credentialData,
                secretData.getValue().getBytes(StandardCharsets.UTF_8),
                policy.getLookAheadWindow());
        otpHolderMap.put(credentialData.getSubType(),
                new OtpHolder(credentialData.getSubType(), credential, totPs));
        return otpHolderMap;
    }
}
