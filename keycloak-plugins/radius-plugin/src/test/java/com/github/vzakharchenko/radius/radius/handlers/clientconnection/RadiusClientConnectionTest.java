package com.github.vzakharchenko.radius.radius.handlers.clientconnection;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import com.github.vzakharchenko.radius.test.ModelBuilder;
import org.tinyradius.packet.AccessRequest;
import org.testng.annotations.Test;

import java.net.InetSocketAddress;

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
        accessRequest.addAttribute("NAS-IP-Address", "192.168.1.1");
        assertEquals(radiusClientConnection.getRemoteAddr(),"192.168.1.1");
    }
    @Test
    public void testRadiusClientConnection2() {
        InetSocketAddress inetSocketAddress =  InetSocketAddress.createUnresolved(ModelBuilder.IP, 0);
        AccessRequest accessRequest = new AccessRequest(realDictionary, 0, new byte[16]);
        RadiusClientConnection radiusClientConnection = new RadiusClientConnection(inetSocketAddress,
                accessRequest);
        assertEquals(radiusClientConnection.getLocalAddr(),"");
        assertEquals(radiusClientConnection.getLocalPort(), 0);
        assertEquals(radiusClientConnection.getRemoteAddr(),"");
    }



}
