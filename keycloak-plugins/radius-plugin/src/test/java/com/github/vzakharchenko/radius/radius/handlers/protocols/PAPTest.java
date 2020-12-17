package com.github.vzakharchenko.radius.radius.handlers.protocols;

import com.github.vzakharchenko.radius.models.OtpHolder;
import com.github.vzakharchenko.radius.radius.handlers.otp.IOtpPasswordFactory;
import com.github.vzakharchenko.radius.radius.handlers.otp.OtpPassword;
import com.github.vzakharchenko.radius.radius.handlers.otp.OtpPasswordInfo;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        hashMap.put("otp", new OtpHolder("otp", new CredentialModel(), Collections.singletonList("123456")));
        when(userCredentialManager.isValid(eq(realmModel), eq(userModel), any(CredentialInput.class))).thenReturn(false);
        otpPasswordInfo = new OtpPassword(false);
        otpPasswordInfo.putAll(hashMap);
        when(passwordFactory.getOTPs(session)).thenReturn(otpPasswordInfo);
    }

    public void enableOTP() {
        Map<String, OtpHolder> otpHolderMap = otpPasswordInfo.getOtpHolderMap();
        otpPasswordInfo = new OtpPassword(false);
        otpPasswordInfo.putAll(otpHolderMap);
        when(passwordFactory.getOTPs(session)).thenReturn(otpPasswordInfo);
    }

    @Test
    public void testPapSuccess() {
        request.setUserPassword("test");
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        assertEquals(papProtocol.getType(), ProtocolType.PAP);
        papProtocol.answer(null, null);
        assertTrue(papProtocol.verifyPassword("test"));
        assertFalse(papProtocol.verifyPassword(null));
        assertFalse(papProtocol.verifyPassword(""));
        assertFalse(papProtocol.verifyPassword("asdf"));


    }

    @Test
    public void testPapKerberosFalse() {
        request.setUserPassword("test");
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        assertFalse(papProtocol.verifyPassword());
    }

    @Test
    public void testPapPasswordSuccess() {
        request.setUserPassword("test");
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        when(userCredentialManager.isValid(eq(realmModel), eq(userModel),
                any(CredentialInput.class))).thenReturn(true);
        assertTrue(papProtocol.verifyPassword());
    }

    @Test
    public void testPapKerberosSuccess() {
        request.setUserPassword("test");
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        when(userCredentialManager.isValid(eq(realmModel), eq(userModel),
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
    }

    @Test
    public void testPasswordOtpPassword() {
        enableOTP();
        request.setUserPassword("test123456");
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        papProtocol.setOtpPasswordGetter(passwordFactory);
        assertTrue(papProtocol.verifyPassword("test123456"));
    }

    @Test
    public void testPasswordOtpPasswordFailed() {
        request.setUserPassword("test1234567");
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        papProtocol.setOtpPasswordGetter(passwordFactory);
        assertFalse(papProtocol.verifyPassword("test1234565"));
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
        when(realmProvider.getRealm(Config.getAdminRealm())).thenReturn(realmModel);
        PAPProtocol papProtocol = new PAPProtocol(request, session);
        assertFalse(papProtocol.isValid(new InetSocketAddress(0)));
    }
}
