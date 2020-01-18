package ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols;

import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.attribute.Attributes;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.attribute.VendorSpecificAttribute;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;

import javax.xml.bind.DatatypeConverter;
import java.util.List;

import static org.testng.Assert.*;

public class MSCHAPV2ProtocolTest extends AbstractRadiusTest {

    private String username = "vassio1";
    private String password = "1";
    private String msChap2Response = "0000beaa25fd93d518e76cf98dd749278fbb000000000000000064441659eef40f9a6d9c0192b36ff507443533b655778705";
    private String msChapChallenge = "72d57222d7801eb8d1c13837e8cfab4b";
    private String ntResponse = "64441659eef40f9a6d9c0192b36ff507443533b655778705";
    private String peerChallenge = "beaa25fd93d518e76cf98dd749278fbb";
    private String authenticator = "549e192eca849fdd5b6ea1e6c34feaad";

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
        MSCHAPV2Protocol papProtocol = new MSCHAPV2Protocol(request, session);
        assertEquals(papProtocol.getType(), ProtocolType.MSCHAPV2);

        papProtocol.answer(new RadiusPacket(realDictionary,1,0), null);
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
                                                "96E84EE736528B3FBB71773C65A4F2D8")));
        request
                .getAttributes()
                .add(
                        Attributes
                                .createAttribute(realDictionary, 311, 25,
                                        DatatypeConverter.parseHexBinary(
                                                "00007EF3351F4F81BEC7643EFEC07534750B00000000000000004D25397B8A3778CD5D7467B62B0934C8F27D6121ADE1C17E")));

        MSCHAPV2Protocol chapProtocol = new MSCHAPV2Protocol(request, session);
        assertFalse(chapProtocol.verifyPassword("1"));
    }

    @Test
    public void testChapV2True() throws DecoderException {
        AccessRequest request = new AccessRequest(realDictionary, 0,
                Hex.decodeHex(authenticator.toCharArray()));
        request.setUserPassword(password);
        request.setUserName(username);

        VendorSpecificAttribute vendorSpecificAttribute = new VendorSpecificAttribute(realDictionary, 311);
        vendorSpecificAttribute.addSubAttribute(Attributes
                .createAttribute(realDictionary, 311, 25,
                        DatatypeConverter.parseHexBinary(
                                "0000beaa25fd93d518e76cf98dd749278fbb000000000000000064441659eef40f9a6d9c0192b36ff507443533b655778705")));
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
        assertTrue(chapProtocol.verifyPassword("1"));
        RadiusPacket answer = new RadiusPacket(realDictionary, 2, 1);
        chapProtocol.answer(answer, radiusUserInfoGetter);
        List<RadiusAttribute> attributes = answer.getAttributes();
        assertEquals(attributes.size(), 1);
        VendorSpecificAttribute vendorSpecific = (VendorSpecificAttribute) attributes.get(0);
        List<RadiusAttribute> subAttributes = vendorSpecific.getSubAttributes();
        assertEquals(subAttributes.size(), 1);
        vendorSpecific = (VendorSpecificAttribute) subAttributes.get(0);
        subAttributes = vendorSpecific.getSubAttributes();
        assertEquals(subAttributes.size(), 5);
        RadiusAttribute subAttribute = vendorSpecific.getSubAttribute("MS-CHAP2-Success");
        assertNotNull(subAttribute);
        assertEquals(subAttribute.getValueString(),"1S=88CE0656242014B2F4C18969FDB1EA3416D37210");


    }
    @Test
    public void testChapV2false() throws DecoderException {
        AccessRequest request = new AccessRequest(realDictionary, 0,
                Hex.decodeHex(authenticator.toCharArray()));
        request.setUserPassword(password);
        request.setUserName(username);

        VendorSpecificAttribute vendorSpecificAttribute = new VendorSpecificAttribute(realDictionary, 311);
        vendorSpecificAttribute.addSubAttribute(Attributes
                .createAttribute(realDictionary, 311, 25,
                        DatatypeConverter.parseHexBinary(
                                "0000beaa25fd93d518e76cf98dd749278fbb000000000000000064441659eef40f9a6d9c0192b36ff507443533b655778705")));
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
        assertFalse(chapProtocol.verifyPassword("2"));
        RadiusPacket answer = new RadiusPacket(realDictionary, 1, 1);
        chapProtocol.answer(answer, radiusUserInfoGetter);
        List<RadiusAttribute> attributes = answer.getAttributes();
        assertEquals(attributes.size(), 0);

    }
}
