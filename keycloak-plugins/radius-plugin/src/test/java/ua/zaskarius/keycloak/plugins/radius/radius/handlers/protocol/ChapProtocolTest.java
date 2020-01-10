package ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocol;

import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.CHAPProtocol;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.attribute.AttributeType;
import org.tinyradius.attribute.Attributes;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.AccessRequest;

import javax.xml.bind.DatatypeConverter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ChapProtocolTest extends AbstractRadiusTest {
    private Dictionary dictionary = mock(Dictionary.class);

    @BeforeMethod
    public void before() {
        when(dictionary.getAttributeTypeByCode(0, 60))
                .thenReturn(new AttributeType(60, "CHAP-Challenge", "octets"));
        when(dictionary.getAttributeTypeByCode(0, 3))
                .thenReturn(new AttributeType(3, "CHAP-Password", "octets"));
    }

    @Test //todo
    public void testChapFail() {
        AccessRequest request = new AccessRequest(dictionary, 0, new byte[16]);
        request
                .getAttributes()
                .add(
                        Attributes
                                .createAttribute(dictionary, 0, 60,
                                        DatatypeConverter.parseHexBinary(
                                                "ad2c7efe802ea7bea94e270404eb01ae")));
        request
                .getAttributes()
                .add(
                        Attributes
                                .createAttribute(dictionary, 0, 3,
                                        DatatypeConverter.parseHexBinary(
                                                "000ad48b2d944948e8014118aeb4e56923")));

        CHAPProtocol chapProtocol = new CHAPProtocol(request, session);
        assertTrue(chapProtocol.verifyPassword("1"));
        assertFalse(chapProtocol.verifyPassword("2"));
        assertFalse(chapProtocol.verifyPassword(null));
        assertFalse(chapProtocol.verifyPassword(""));
    }
}
