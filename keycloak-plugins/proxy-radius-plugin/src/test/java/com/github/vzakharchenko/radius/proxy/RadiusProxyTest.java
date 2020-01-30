package com.github.vzakharchenko.radius.proxy;

import com.github.vzakharchenko.radius.proxy.proxy.IComponentProxy;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.mockito.Mock;
import org.testng.annotations.Test;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.packet.RadiusPackets;
import org.tinyradius.server.RequestCtx;
import org.tinyradius.util.RadiusEndpoint;

import java.net.InetSocketAddress;

import static com.github.vzakharchenko.radius.proxy.RadiusProxy.RADIUS_PROXY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class RadiusProxyTest extends AbstractRadiusTest {
    private RadiusProxy radiusProxy = new RadiusProxy();
    @Mock
    private IComponentProxy componentProxy;

    private RadiusPacket radiusPacket;
    private RadiusEndpoint radiusEndpoint;

    @Test
    public void beforeMethods() {
        reset(componentProxy);
        radiusProxy.setComponentProxy(componentProxy);
        radiusPacket = RadiusPackets.create(realDictionary, 1, 1);
        when(componentProxy.proxy(any(), any(), any())).thenReturn(radiusPacket);
        radiusEndpoint = new RadiusEndpoint(new InetSocketAddress(0), "test");
    }

    @Test
    public void testMethods() {
        radiusProxy.close();
        radiusProxy.init(null);
        radiusProxy.postInit(null);
        assertEquals(radiusProxy.create(session), radiusProxy);
        assertEquals(radiusProxy.getId(), RADIUS_PROXY);
    }

    @Test
    public void testProxyAccessRequest() {
        RequestCtx requestCtx = new RequestCtx(
                new AccessRequest(realDictionary, 1, new byte[16]), radiusEndpoint);
        RadiusPacket proxy = radiusProxy.proxy(session, requestCtx, RadiusPackets.create(realDictionary, 3, 1));
        assertEquals(proxy, radiusPacket);
    }

    @Test
    public void testProxyAccountingRequest() {
        RequestCtx requestCtx = new RequestCtx(
                new AccountingRequest(realDictionary, 1, new byte[16]), radiusEndpoint);
        RadiusPacket proxy = radiusProxy.proxy(session, requestCtx, RadiusPackets.create(realDictionary, 3, 1));
        assertEquals(proxy, radiusPacket);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testProxyError() {
        RequestCtx requestCtx = new RequestCtx(
                RadiusPackets.create(realDictionary, 41, 1, new byte[16]), radiusEndpoint);
        radiusProxy.proxy(session, requestCtx, RadiusPackets.create(realDictionary, 3, 1));
    }
}
