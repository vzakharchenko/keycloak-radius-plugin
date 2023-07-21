package com.github.vzakharchenko.radius.radius.handlers.otp;

import com.github.vzakharchenko.radius.models.OtpHolder;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.OTPPolicy;
import org.keycloak.models.credential.dto.OTPCredentialData;
import org.keycloak.models.credential.dto.OTPSecretData;
import org.keycloak.models.utils.HmacOTP;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.keycloak.models.credential.OTPCredentialModel.HOTP;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class TotpPasswordTest {
    public static final String ALGORITHM = "sha1";
    private final TotpPassword totpPassword = new TotpPassword();

    @Test
    public void testHotpPassword() {
        OTPCredentialData credentialData =
                new OTPCredentialData(HOTP, 6, 1, 1, HmacOTP.HMAC_SHA1, null);
        OTPPolicy policy = new OTPPolicy(HOTP, ALGORITHM,
                1, 6, 1, 1);
        CredentialModel credential = new CredentialModel();
        Map<String, OtpHolder> otpPasswords = totpPassword
                .getOTPPasswords(credentialData, policy,
                        new OTPSecretData("1"), credential);
        assertNotNull(otpPasswords);
        OtpHolder otpHolder = otpPasswords.get(HOTP);
        assertEquals(otpHolder.getCredentialModel(), credential);
        assertEquals(otpHolder.getSubType(), HOTP);
        List<String> passwords = otpHolder.getPasswords();
        assertEquals(passwords.size(), 2);
    }
}
