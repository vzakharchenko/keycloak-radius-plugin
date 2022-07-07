package com.github.vzakharchenko.radius.radius.handlers.protocols;

import com.github.vzakharchenko.radius.models.OtpHolder;
import com.github.vzakharchenko.radius.radius.handlers.otp.IOtpPasswordFactory;
import com.github.vzakharchenko.radius.radius.handlers.otp.OtpPassword;
import com.github.vzakharchenko.radius.radius.handlers.otp.OtpPasswordInfo;
import com.github.vzakharchenko.radius.radius.handlers.session.PasswordData;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.keycloak.Config;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialModel;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.packet.AccessRequest;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class PAPTest extends AbstractRadiusTest {
    private AccessRequest request;
    private OtpPasswordInfo otpPasswordInfo;

    @Mock
    private IOtpPasswordFactory passwordFactory;

    @BeforeMethod
    public void beforeMethods() {
        reset(passwordFactory);
        request = new AccessRequest(realDictionary, 0, new byte[16]);
        request.addAttribute(REALM_RADIUS, REALM_RADIUS_NAME);
        HashMap<String, OtpHolder> hashMap = new HashMap<>();
        hashMap.put("otp1",
                new OtpHolder("otp", new CredentialModel(), List.of("123456", "234567")));
        hashMap.put("otp2",
                new OtpHolder("otp", new CredentialModel(), List.of("876543","987654")));
        when(subjectCredentialManager.isValid(any(CredentialInput.class))).thenReturn(false);
        otpPasswordInfo = new OtpPassword(false, clientModel);
        otpPasswordInfo.putAll(hashMap);
        when(passwordFactory.getOTPs(session)).thenReturn(otpPasswordInfo);
    }

    public void enableOTP() {
        Map<String, OtpHolder> otpHolderMap = otpPasswordInfo.getOtpHolderMap();
        otpPasswordInfo = new OtpPassword(false, clientModel);
        otpPasswordInfo.putAll(otpHolderMap);
        when(passwordFactory.getOTPs(session)).thenReturn(otpPasswordInfo);
    }


    @Test
    public void testPapVerifyPasswordRadius() {
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        assertEquals(papProtocol.getType(), ProtocolType.PAP);
        papProtocol.answer(null, null);

        when(subjectCredentialManager.isValid(any(CredentialInput.class)))
                .thenThrow(new RuntimeException("should not be called"));

        request.setUserPassword("test");
        assertTrue(papProtocol.verifyPassword(PasswordData.create("test")));
        assertFalse(papProtocol.verifyPassword(null));
        assertFalse(papProtocol.verifyPassword(PasswordData.create("")));
        assertFalse(papProtocol.verifyPassword(PasswordData.create("asdf")));

        request.setUserPassword("test123456");
        assertFalse(papProtocol.verifyPassword(PasswordData.create("test")));
        request.setUserPassword("123456");
        assertFalse(papProtocol.verifyPassword(PasswordData.create("test")));
    }

    @Test
    public void testPapVerifyPasswordRadiusWithOtp() {
        enableOTP();
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        papProtocol.setOtpPasswordGetter(passwordFactory);
        assertEquals(papProtocol.getType(), ProtocolType.PAP);
        papProtocol.answer(null, null);

        when(subjectCredentialManager.isValid(any(CredentialInput.class)))
                .thenThrow(new RuntimeException("should not be called"));

        request.setUserPassword("test");
        assertFalse(papProtocol.verifyPassword(PasswordData.create("test")));
        request.setUserPassword("test123456");
        assertTrue(papProtocol.verifyPassword(PasswordData.create("test")));
        request.setUserPassword("test999999");
        assertFalse(papProtocol.verifyPassword(PasswordData.create("test")));
        request.setUserPassword("TEST123456");
        assertFalse(papProtocol.verifyPassword(PasswordData.create("test")));
        request.setUserPassword("123456");
        assertFalse(papProtocol.verifyPassword(PasswordData.create("test")));

        enableOtpWithoutPassword();
        request.setUserPassword("test");
        assertFalse(papProtocol.verifyPassword(PasswordData.create("test")));
        request.setUserPassword("test123456");
        assertTrue(papProtocol.verifyPassword(PasswordData.create("test")));
        request.setUserPassword("test999999");
        assertFalse(papProtocol.verifyPassword(PasswordData.create("test")));
        request.setUserPassword("TEST123456");
        assertFalse(papProtocol.verifyPassword(PasswordData.create("test")));
        request.setUserPassword("123456");
        assertTrue(papProtocol.verifyPassword(PasswordData.create("test")));
    }

    @Test
    public void testPapKerberosFalse() {
        request.setUserPassword("test");
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        papProtocol.setOtpPasswordGetter(passwordFactory);
        assertFalse(papProtocol.verifyPassword());
    }

    @Test
    public void testPapPasswordSuccess() {
        request.setUserPassword("test");
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        when(subjectCredentialManager.isValid(
                any(CredentialInput.class))).thenAnswer(
                i -> i.getArgument(0, CredentialInput.class).getChallengeResponse().equals("test"));
        assertTrue(papProtocol.verifyPassword());
    }

    @Test
    public void testPapKerberosSuccess() {
        request.setUserPassword("test");
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        when(subjectCredentialManager.isValid(
                any(CredentialInput.class))).thenReturn(false, true);
        assertTrue(papProtocol.verifyPassword());
    }

    @Test
    public void testOtpPassword() {
        enableOTP();
        request.setUserPassword("123456");
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        papProtocol.setOtpPasswordGetter(passwordFactory);

        assertFalse(papProtocol.verifyPassword());

        enableOtpWithoutPassword();
        assertTrue(papProtocol.verifyPassword());
    }

    @Test
    public void testOtpPasswordOTPWithoutPassword() {
        enableOTP();
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        papProtocol.setOtpPasswordGetter(passwordFactory);

        when(subjectCredentialManager.isValid(any(CredentialInput.class)))
                .thenThrow(new RuntimeException("should not be called"));

        request.setUserPassword("123456");
        assertFalse(papProtocol.verifyPassword());

        request.setUserPassword("999999");
        assertFalse(papProtocol.verifyPassword());

        enableOtpWithoutPassword();
        request.setUserPassword("123456");
        assertTrue(papProtocol.verifyPassword());

        request.setUserPassword("999999");
        assertFalse(papProtocol.verifyPassword());
    }


    @Test
    public void testOtpPasswordWithoutPassword() {
        otpPasswordInfo = new OtpPassword(false, clientModel);
        when(passwordFactory.getOTPs(session)).thenReturn(otpPasswordInfo);
        request.setUserPassword("123456");
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        papProtocol.setOtpPasswordGetter(passwordFactory);

        assertFalse(papProtocol.verifyPassword());

        enableOtpWithoutPassword();
        assertFalse(papProtocol.verifyPassword());
    }

    @Test
    public void testPasswordOtpPassword() {
        enableOTP();
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        papProtocol.setOtpPasswordGetter(passwordFactory);

        when(subjectCredentialManager.isValid(
                any(CredentialInput.class))).thenAnswer(
                i -> i.getArgument(0, CredentialInput.class).getChallengeResponse().equals("test"));

        request.setUserPassword("test123456");
        assertTrue(papProtocol.verifyPassword(PasswordData.create("test")));
        request.setUserPassword("123456");
        assertFalse(papProtocol.verifyPassword(PasswordData.create("test")));
        request.setUserPassword("test");
        assertFalse(papProtocol.verifyPassword(PasswordData.create("test")));

        enableOtpWithoutPassword();
        request.setUserPassword("test123456");
        assertTrue(papProtocol.verifyPassword(PasswordData.create("test")));
        request.setUserPassword("123456");
        assertTrue(papProtocol.verifyPassword(PasswordData.create("test")));
        request.setUserPassword("test");
        assertFalse(papProtocol.verifyPassword(PasswordData.create("test")));
    }

    @Test
    public void testPasswordOtpPasswordFailed() {
        request.setUserPassword("test1234567");
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        papProtocol.setOtpPasswordGetter(passwordFactory);
        assertFalse(papProtocol.verifyPassword(PasswordData.create("test2")));
    }

    @Test
    public void testNoOtpPassword() {
        request.setUserPassword("1234567");
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        papProtocol.setOtpPasswordGetter(passwordFactory);
        assertFalse(papProtocol.verifyPassword());
    }

    @Test
    public void testAccessRequest() {
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        AccessRequest accessRequest = papProtocol.getAccessRequest();
        assertNotNull(accessRequest);
    }

    @Test
    public void testIsValid() {
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        assertTrue(papProtocol.isValid(new InetSocketAddress(0)));
    }

    @Test
    public void testIsNotValid() {
        // request.addAttribute(REALM_RADIUS, "33");
        reset(realmProvider);
        when(realmProvider.getRealmByName(Config.getAdminRealm())).thenReturn(realmModel);
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        assertFalse(papProtocol.isValid(new InetSocketAddress(0)));
    }
}
