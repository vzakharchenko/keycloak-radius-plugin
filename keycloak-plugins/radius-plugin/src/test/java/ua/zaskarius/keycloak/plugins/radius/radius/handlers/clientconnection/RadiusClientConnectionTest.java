package ua.zaskarius.keycloak.plugins.radius.radius.handlers.clientconnection;

import ua.zaskarius.keycloak.plugins.radius.test.ModelBuilder;
import org.testng.annotations.Test;

import java.net.InetSocketAddress;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RadiusClientConnectionTest {
    @Test
    public void testRadiusClientConnection() {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ModelBuilder.IP, 0);
        RadiusClientConnection radiusClientConnection = new RadiusClientConnection(inetSocketAddress);
        assertNotNull(radiusClientConnection.getLocalAddr());
        assertEquals(radiusClientConnection.getLocalPort(), 0);
        assertNotNull(radiusClientConnection.getRemoteAddr());
        assertNotNull(radiusClientConnection.getInetSocketAddress());
        assertNotNull(radiusClientConnection.getRemoteHost());
        assertEquals(radiusClientConnection.getRemotePort(), 0);
    }
}
