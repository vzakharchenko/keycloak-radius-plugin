package ua.zaskarius.keycloak.plugins.radius.radius.handlers.clientconnection;

import org.apache.commons.codec.binary.Hex;
import org.tinyradius.packet.AccessRequest;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import ua.zaskarius.keycloak.plugins.radius.test.ModelBuilder;
import org.testng.annotations.Test;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RadiusClientConnectionTest extends AbstractRadiusTest {
    @Test
    public void testRadiusClientConnection() {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ModelBuilder.IP, 0);
        AccessRequest accessRequest = new AccessRequest(realDictionary, 0, new byte[16]);
        RadiusClientConnection radiusClientConnection = new RadiusClientConnection(inetSocketAddress,
                accessRequest);
        assertEquals(radiusClientConnection.getLocalAddr(),"");
        assertEquals(radiusClientConnection.getLocalPort(), 0);
        assertNotNull(radiusClientConnection.getRemoteAddr());
        assertNotNull(radiusClientConnection.getInetSocketAddress());
        assertEquals(radiusClientConnection.getRemoteHost(),"123.123.123.123");
        assertEquals(radiusClientConnection.getRemotePort(), 0);
        accessRequest.addAttribute("Calling-Station-Id", "192.168.1.1");
        assertEquals(radiusClientConnection.getLocalAddr(),"192.168.1.1");
    }
}
