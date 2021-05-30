package com.github.vzakharchenko.radius.radius.handlers.protocols;

import com.github.vzakharchenko.radius.configuration.RadiusConfigHelper;
import com.github.vzakharchenko.radius.models.OtpHolder;
import com.github.vzakharchenko.radius.radius.handlers.otp.IOtpPasswordFactory;
import com.github.vzakharchenko.radius.radius.handlers.otp.OtpPassword;
import com.github.vzakharchenko.radius.radius.handlers.otp.OtpPasswordInfo;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import com.github.vzakharchenko.radius.test.ModelBuilder;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialModel;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.attribute.AttributeType;
import org.tinyradius.attribute.Attributes;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.AccessRequest;

import javax.xml.bind.DatatypeConverter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ChapProtocolTest extends AbstractRadiusTest {
    private final Dictionary dictionary = mock(Dictionary.class);
    private AccessRequest request;
    private OtpPasswordInfo otpPasswordInfo;

    @Mock
    private IOtpPasswordFactory passwordFactory;

    public void enableOTP() {
        Map<String, OtpHolder> otpHolderMap = otpPasswordInfo.getOtpHolderMap();
        otpPasswordInfo = new OtpPassword(false);
        otpPasswordInfo.putAll(otpHolderMap);
        when(passwordFactory.getOTPs(session)).thenReturn(otpPasswordInfo);
    }

    public void beforeMethods() {
        HashMap<String, OtpHolder> hashMap = new HashMap<>();
        hashMap.put("otp", new OtpHolder("otp", new CredentialModel(),
                Collections.singletonList("1")));
        when(userCredentialManager.isValid(eq(realmModel), eq(userModel),
                any(CredentialInput.class))).thenReturn(false);
        otpPasswordInfo = new OtpPassword(false);
        otpPasswordInfo.putAll(hashMap);
        when(passwordFactory.getOTPs(session)).thenReturn(otpPasswordInfo);
        when(dictionary.getAttributeTypeByCode(0, 60))
                .thenReturn(new AttributeType(60, "CHAP-Challenge", "octets"));
        when(dictionary.getAttributeTypeByCode(0, 3))
                .thenReturn(new AttributeType(3, "CHAP-Password", "octets"));
    }

    @BeforeMethod
    public void before() {
        reset(passwordFactory);
        request = new AccessRequest(realDictionary, 0, new byte[16]);
        request
                .getAttributes()
                .add(
                        Attributes
                                .createAttribute(realDictionary, 0, 60,
                                        DatatypeConverter.parseHexBinary(
                                                "ad2c7efe802ea7bea94e270404eb01ae")));
        request
                .getAttributes()
                .add(
                        Attributes
                                .createAttribute(realDictionary, 0, 3,
                                        DatatypeConverter.parseHexBinary(
                                                "000ad48b2d944948e8014118aeb4e56923")));
        request.addAttribute(REALM_RADIUS, REALM_RADIUS_NAME);
        beforeMethods();
    }

    @Test
    public void testChapFail() {


        CHAPProtocol chapProtocol = new CHAPProtocol(request, session);
        assertEquals(chapProtocol.getType(), ProtocolType.CHAP);
        chapProtocol.answer(null, null);
        assertTrue(chapProtocol.verifyPassword("1"));
        assertFalse(chapProtocol.verifyPassword("2"));
        assertFalse(chapProtocol.verifyPassword(null));
        assertFalse(chapProtocol.verifyPassword(""));
        assertFalse(chapProtocol.verifyPassword());
    }

    @Test
    public void testOtpPasswordValid() {
        enableOTP();
        reset(configuration);
        when(configuration.getRadiusSettings())
                .thenReturn(ModelBuilder.createRadiusOtpServerSettings());
        RadiusConfigHelper.setConfiguration(configuration);
        CHAPProtocol chapProtocol = new CHAPProtocol(request, session);
        chapProtocol.setOtpPasswordGetter(passwordFactory);
        assertTrue(chapProtocol.verifyPassword());
    }
}
