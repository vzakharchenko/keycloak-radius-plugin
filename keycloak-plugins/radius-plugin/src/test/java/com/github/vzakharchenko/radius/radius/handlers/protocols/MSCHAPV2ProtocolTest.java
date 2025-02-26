package com.github.vzakharchenko.radius.radius.handlers.protocols;

import com.github.vzakharchenko.radius.radius.handlers.session.PasswordData;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.attribute.Attributes;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.attribute.VendorSpecificAttribute;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;

import jakarta.xml.bind.DatatypeConverter;
import java.util.List;

import static org.testng.Assert.*;

public class MSCHAPV2ProtocolTest extends AbstractRadiusTest {

    private final String username = "vassio1";
    private final String password = "1";
    private final String msChap2Response = "0000beaa25fd93d518e76cf98dd749278fbb00000" +
            "0000000000064441659eef40f9a6d9c0192b36ff507443533b655778705";
    private final String msChapChallenge = "72d57222d7801eb8d1c13837e8cfab4b";
    private final String ntResponse = "64441659eef40f9a6d9c0192b36ff507443533b655778705";
    private final String peerChallenge = "beaa25fd93d518e76cf98dd749278fbb";
    private final String authenticator = "549e192eca849fdd5b6ea1e6c34feaad";

    public MSCHAPV2ProtocolTest() {

    }

    @BeforeMethod
    public void before() {

//        when(dictionary.getAttributeTypeByCode(311, 11))
//                .thenReturn(new AttributeType(11,
//                        "CHAP-Challenge", "octets"));
//        when(dictionary.getAttributeTypeByCode(311, 25))
//                .thenReturn(new AttributeType(25,
//                        "CHAP-Password", "octets"));
    }

    @Test
    public void testChapMethods() {
        AccessRequest request = new AccessRequest(realDictionary, 0, new byte[16]);
        MSCHAPV2Protocol protocol = new MSCHAPV2Protocol(request, session);
        assertEquals(protocol.getType(), ProtocolType.MSCHAPV2);

        protocol.answer(
                new RadiusPacket(realDictionary, 1, 0), null);
    }

    @Test
    public void testChapV2False1() {
        AccessRequest request = new AccessRequest(realDictionary, 0, new byte[16]);
        request
                .getAttributes()
                .add(
                        Attributes
                                .createAttribute(realDictionary, 311, 11,
                                        DatatypeConverter.parseHexBinary(
                                                "96E84EE736528B3FBB7177" +
                                                        "3C65A4F2D8")));
        request
                .getAttributes()
                .add(
                        Attributes
                                .createAttribute(realDictionary, 311, 25,
                                        DatatypeConverter.parseHexBinary(
                                                "00007EF3351F4F81BEC7643EFEC" +
                                                        "07534750B00000000000000004D25397B" +
                                                        "8A3778CD5D7467B62B0934C8F27D61" +
                                                        "21ADE1C17E")));

        MSCHAPV2Protocol chapProtocol = new MSCHAPV2Protocol(request, session);
        assertFalse(chapProtocol.verifyPassword(PasswordData.create("1")));
    }

    @Test
    public void testChapV2True() throws DecoderException {
        AccessRequest request = new AccessRequest(realDictionary, 0,
                Hex.decodeHex(authenticator.toCharArray()));
        request.setUserPassword(password);
        request.setUserName(username);

        VendorSpecificAttribute vendorSpecificAttribute =
                new VendorSpecificAttribute(realDictionary, 311);
        vendorSpecificAttribute.addSubAttribute(Attributes
                .createAttribute(realDictionary, 311, 25,
                        DatatypeConverter.parseHexBinary(
                                "0000beaa25fd93d518e76cf98dd749278fbb" +
                                        "000000000000000064441659eef40f9a6d9c0192b36ff50" +
                                        "7443533b655778705")));
        vendorSpecificAttribute.addSubAttribute(Attributes
                .createAttribute(realDictionary, 311, 11,
                        DatatypeConverter.parseHexBinary(
                                "72d57222d7801eb8d1c13837e8cfab4b")));

        request
                .getAttributes()
                .add(
                        vendorSpecificAttribute
                );

        MSCHAPV2Protocol chapProtocol = new MSCHAPV2Protocol(request, session);
        assertTrue(chapProtocol.verifyPassword(PasswordData.create("1")));
        RadiusPacket answer = new RadiusPacket(realDictionary, 2, 1);
        chapProtocol.answer(answer, radiusUserInfoGetter);
        List<RadiusAttribute> attributes = answer.getAttributes();
        assertEquals(attributes.size(), 1);
        VendorSpecificAttribute vendorSpecific = (VendorSpecificAttribute) attributes.getFirst();
        List<RadiusAttribute> subAttributes = vendorSpecific.getSubAttributes();
        assertEquals(subAttributes.size(), 5);
        RadiusAttribute subAttribute = vendorSpecific.getSubAttribute("MS-CHAP2-Success");
        assertNotNull(subAttribute);
        assertEquals(subAttribute.getValueString(),
                "1S=88CE0656242014B2F4C18969FDB1EA3416D37210");


    }

    @Test
    public void testChapV2false() throws DecoderException {
        AccessRequest request = new AccessRequest(realDictionary, 0,
                Hex.decodeHex(authenticator.toCharArray()));
        request.setUserPassword(password);
        request.setUserName(username);

        VendorSpecificAttribute vendorSpecificAttribute =
                new VendorSpecificAttribute(realDictionary, 311);
        vendorSpecificAttribute.addSubAttribute(Attributes
                .createAttribute(realDictionary, 311, 25,
                        DatatypeConverter.parseHexBinary(
                                "0000beaa25fd93d5" +
                                        "18e76cf98dd749278fbb0000000" +
                                        "00000000064441659eef40f9a6d9c0" +
                                        "192b36ff507443533b655778705")));
        vendorSpecificAttribute.addSubAttribute(Attributes
                .createAttribute(realDictionary, 311, 11,
                        DatatypeConverter.parseHexBinary(
                                "72d57222d7801eb8d1c13837e8cfab4b")));

        request
                .getAttributes()
                .add(
                        vendorSpecificAttribute
                );

        MSCHAPV2Protocol chapProtocol = new MSCHAPV2Protocol(request, session);
        assertFalse(chapProtocol.verifyPassword(PasswordData.create("2")));
        RadiusPacket answer = new RadiusPacket(realDictionary, 1, 1);
        chapProtocol.answer(answer, radiusUserInfoGetter);
        List<RadiusAttribute> attributes = answer.getAttributes();
        assertEquals(attributes.size(), 0);
        assertFalse(chapProtocol.verifyPassword());
    }
}
