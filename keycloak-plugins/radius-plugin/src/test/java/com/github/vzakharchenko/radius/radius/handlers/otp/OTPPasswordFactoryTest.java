package com.github.vzakharchenko.radius.radius.handlers.otp;

import com.github.vzakharchenko.radius.models.OtpHolder;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.credential.OTPCredentialProvider;
import org.keycloak.credential.OTPCredentialProviderFactory;
import org.keycloak.models.OTPPolicy;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.OTPCredentialModel;
import org.keycloak.models.utils.HmacOTP;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

import static org.keycloak.models.credential.OTPCredentialModel.HOTP;
import static org.keycloak.models.credential.OTPCredentialModel.TOTP;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class OTPPasswordFactoryTest extends AbstractRadiusTest {
    public static final String CRED_ID = "credId";
    private final OTPPasswordFactory otpPasswordFactory = new OTPPasswordFactory();
    private final OTPPolicy otpPolicy = new OTPPolicy();

    @BeforeMethod
    public void beforeMethods() {
        OTPCredentialModel credentialModelOtp = OTPCredentialModel
                .createTOTP("secret", 6, 1,
                        HmacOTP.DEFAULT_ALGORITHM);
        credentialModelOtp.setId(CRED_ID);
        OTPCredentialModel credentialModelHotp = OTPCredentialModel
                .createHOTP("secret", 6, 1, HmacOTP.DEFAULT_ALGORITHM);
        credentialModelHotp.setId(CRED_ID);
        otpPolicy.setType(TOTP);
        otpPolicy.setAlgorithm(HmacOTP.DEFAULT_ALGORITHM);
        otpPolicy.setDigits(6);
        otpPolicy.setInitialCounter(1);
        otpPolicy.setPeriod(1);
        when(subjectCredentialManager
                .getStoredCredentialsByTypeStream(OTPCredentialModel.TYPE))
                .thenAnswer(i -> Stream.of(credentialModelOtp, credentialModelHotp));
        when(subjectCredentialManager.getStoredCredentialById(CRED_ID))
                .thenReturn(credentialModelOtp);
        when(realmModel.getOTPPolicy()).thenReturn(otpPolicy);
        when(session
                .getProvider(CredentialProvider.class,
                        OTPCredentialProviderFactory.PROVIDER_ID))
                .thenReturn(new OTPCredentialProvider(session));
    }

    @Test
    public void testGetOTPs() {
        OtpPasswordInfo otpPasswordInfo = otpPasswordFactory.getOTPs(session);
        Map<String, OtpHolder> otPs = otpPasswordInfo.getOtpHolderMap();
        assertEquals(otPs.size(), 1);
        OtpHolder otpHolder = otPs.get(CRED_ID);
        assertNotNull(otpHolder);
        assertEquals(otpHolder.getSubType(), TOTP);
        assertEquals(otpHolder.getPasswords().size(), 1);
        assertNotNull(otpHolder.getPasswords().getFirst());
        assertEquals(otpHolder.getPasswords().getFirst().length(), 6);
    }

    @Test
    public void testGetOTPsRequiredAction() {
        when(userModel.getRequiredActionsStream())
                .thenAnswer(i -> Stream.of(UserModel.RequiredAction.CONFIGURE_TOTP.name()));
        when(subjectCredentialManager
                .getStoredCredentialsByTypeStream(OTPCredentialModel.TYPE))
                .thenReturn(new ArrayList<CredentialModel>().stream());
        OtpPasswordInfo otpPasswordInfo = otpPasswordFactory.getOTPs(session);
        Map<String, OtpHolder> otPs = otpPasswordInfo.getOtpHolderMap();
        assertEquals(otPs.size(), 0);
    }

    @Test
    public void testGetHOTPs() {
        otpPolicy.setType(HOTP);
        OtpPasswordInfo otpPasswordInfo = otpPasswordFactory.getOTPs(session);
        Map<String, OtpHolder> otPs = otpPasswordInfo.getOtpHolderMap();
        assertEquals(otPs.size(), 1);
        OtpHolder otpHolder = otPs.get(CRED_ID);
        assertNotNull(otpHolder);
        assertEquals(otpHolder.getSubType(), HOTP);
        assertEquals(otpHolder.getPasswords().size(), 1);
        assertNotNull(otpHolder.getPasswords().getFirst());
        assertEquals(otpHolder.getPasswords().getFirst().length(), 6);
    }

    @Test
    public void testValidOTP() {
        otpPasswordFactory.validOTP(session,
                "1234",
                CRED_ID,
                OTPCredentialModel.TYPE);
    }
}
