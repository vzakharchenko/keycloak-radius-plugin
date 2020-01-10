package ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocol;

import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.PAPProtocol;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.AccessRequest;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class PAPProtocolTest extends AbstractRadiusTest {
    private Dictionary dictionary = mock(Dictionary.class);
    private AccessRequest request = new AccessRequest(dictionary, 0, new byte[16]);

    @Test
    public void testPapSuccess() {
        request.setUserPassword("test");
        PAPProtocol papProtocol = new PAPProtocol(request,session);
        assertTrue(papProtocol.verifyPassword("test"));
        assertFalse(papProtocol.verifyPassword(null));
        assertFalse(papProtocol.verifyPassword(""));
        assertFalse(papProtocol.verifyPassword("asdf"));
    }
}
