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

import static org.keycloak.models.credential.OTPCredentialModel.TOTP;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class TotpPasswordTest {
    private static final String ALGORITHM = "sha1";
    private static final String TOTP_ID = "myTotpId";
    private final TotpPassword totpPassword = new TotpPassword();

    @Test
    public void testTotpPassword() {
        testTotpPasswordInternal(TOTP_ID);
    }

    @Test
    public void testTotpPasswordWithNullId() {
        testTotpPasswordInternal(null);
    }

    private void testTotpPasswordInternal(String credentialId) {
        OTPCredentialData credentialData =
                new OTPCredentialData(TOTP, 6, 1, 1, HmacOTP.HMAC_SHA1, null);
        OTPPolicy policy = new OTPPolicy(TOTP, ALGORITHM, 1, 6, 1, 1);
        CredentialModel credential = new CredentialModel();
        if (credentialId != null) {
            credential.setId(credentialId);
        }
        Map<String, OtpHolder> otpPasswords = totpPassword
                .getOTPPasswords(credentialData, policy, new OTPSecretData("1"), credential);
        assertNotNull(otpPasswords);
        OtpHolder otpHolder = otpPasswords.get(credentialId);
        assertNotNull(otpHolder);
        assertEquals(otpHolder.getCredentialModel(), credential);
        assertEquals(otpHolder.getSubType(), TOTP);
        List<String> passwords = otpHolder.getPasswords();
        assertEquals(passwords.size(), 2);
        assertEquals(passwords.get(0).length(), 6);
        assertEquals(passwords.get(1).length(), 6);
    }
}