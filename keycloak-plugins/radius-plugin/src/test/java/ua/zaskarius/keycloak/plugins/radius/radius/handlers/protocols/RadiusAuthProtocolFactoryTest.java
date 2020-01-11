package ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.AccessRequest;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RadiusAuthProtocolFactoryTest extends AbstractRadiusTest {
    private Dictionary dictionary = mock(Dictionary.class);
    private AuthProtocolFactory radiusAuthProtocolFactory = RadiusAuthProtocolFactory
            .getInstance();

    @BeforeMethod
    public void beforeTest() {
        reset(dictionary);
    }

    @Test
    public void testPap() {
        AccessRequest request = new AccessRequest(dictionary, 0, new byte[16]);
        request.setAuthProtocol(AccessRequest.AUTH_PAP);
        AuthProtocol authProtocol = radiusAuthProtocolFactory.create(request, session);
        assertNotNull(authProtocol);
        assertEquals(authProtocol.getClass().getCanonicalName(),
                PAPProtocol.class.getCanonicalName());
    }


    @Test
    public void testCHap() {
        AccessRequest request = new AccessRequest(dictionary, 0, new byte[16]);
        request.setAuthProtocol(AccessRequest.AUTH_CHAP);
        AuthProtocol authProtocol = radiusAuthProtocolFactory.create(request, session);
        assertNotNull(authProtocol);
        assertEquals(authProtocol.getClass().getCanonicalName(),
                CHAPProtocol.class.getCanonicalName());
    }

    @Test
    public void testCHapV2() {
        AccessRequest request = new AccessRequest(dictionary, 0, new byte[16]);
        request.setAuthProtocol(AccessRequest.AUTH_MS_CHAP_V2);
        AuthProtocol authProtocol = radiusAuthProtocolFactory.create(request, session);
        assertNotNull(authProtocol);
        assertEquals(authProtocol.getClass().getCanonicalName(),
                MSCHAPV2Protocol.class.getCanonicalName());
    }


    @Test(expectedExceptions = UnsupportedOperationException.class,
            expectedExceptionsMessageRegExp = "eap verification not supported yet")
    public void testEAP() {
        AccessRequest request = new AccessRequest(dictionary, 0, new byte[16]);
        request.setAuthProtocol(AccessRequest.AUTH_EAP);
        radiusAuthProtocolFactory.create(request, session);
    }
}
