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

public class HotpPasswordTest {
    private static final String ALGORITHM = "sha1";
    private static final String HOTP_ID = "myHotpId";
    private final HotpPassword hotpPassword = new HotpPassword();

    @Test
    public void testHotpPassword() {
        testHotpPasswordInternal(HOTP_ID);
    }

    @Test
    public void testHotpPasswordWithNullId() {
        testHotpPasswordInternal(null);
    }

    private void testHotpPasswordInternal(String credentialId) {
        OTPCredentialData credentialData =
                new OTPCredentialData(HOTP, 6, 1, 1, HmacOTP.HMAC_SHA1, null);
        OTPPolicy policy = new OTPPolicy(HOTP, ALGORITHM, 1, 6, 1, 1);
        CredentialModel credential = new CredentialModel();
        if (credentialId != null) {
           credential.setId(credentialId);
        }
        Map<String, OtpHolder> otpPasswords = hotpPassword
                .getOTPPasswords(credentialData, policy, new OTPSecretData("1"), credential);
        assertNotNull(otpPasswords);
        OtpHolder otpHolder = otpPasswords.get(credentialId);
        assertNotNull(otpHolder);
        assertEquals(otpHolder.getCredentialModel(), credential);
        assertEquals(otpHolder.getSubType(), HOTP);
        List<String> passwords = otpHolder.getPasswords();
        assertEquals(passwords.size(), 2);
        assertEquals(passwords.get(0), "711154");
        assertEquals(passwords.get(1), "844269");
    }
}