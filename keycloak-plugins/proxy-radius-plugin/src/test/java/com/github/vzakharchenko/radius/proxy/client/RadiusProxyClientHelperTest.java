package com.github.vzakharchenko.radius.proxy.client;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.packet.RadiusPackets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

public class RadiusProxyClientHelperTest extends AbstractRadiusTest {
    private RadiusPacket radiusPacketOk;
    private RadiusPacket radiusPacketException;

    @BeforeMethod
    public void beforeMethods() {
        radiusPacketOk = RadiusPackets.create(realDictionary, 2, 1);
        radiusPacketException = RadiusPackets.create(realDictionary, 3, 1);
    }

    @Test
    public void testCoaClient() {
        RadiusProxyClientHelper.setRadiusProxyClient(new RadiusProxyClient());
        assertEquals(RadiusProxyClientHelper.requestProxy(realDictionary, radiusClient -> {
            return radiusPacketOk;
        }, null), radiusPacketOk);
    }

    @Test
    public void testCoaClientException() {
        IRadiusProxyClient radiusProxyClient = mock(IRadiusProxyClient.class);
        RadiusProxyClientHelper.setRadiusProxyClient(radiusProxyClient);
        doThrow(new IllegalStateException("test")).when(radiusProxyClient).requestProxy(any(), any());
        assertEquals(RadiusProxyClientHelper.requestProxy(realDictionary, radiusClient -> {
            return radiusPacketOk;
        }, ex -> radiusPacketException), radiusPacketException);
    }

    @Test
    public void testCoaClientException2() {
        IllegalStateException test = new IllegalStateException("test");
        IRadiusProxyClient radiusProxyClient = mock(IRadiusProxyClient.class);
        RadiusProxyClientHelper.setRadiusProxyClient(radiusProxyClient);
        doThrow(test).when(radiusProxyClient).requestProxy(any(), any());
        RadiusProxyClientHelper.setRadiusProxyClient(radiusProxyClient);
        assertEquals(RadiusProxyClientHelper.requestProxy(realDictionary, radiusClient -> {
            return radiusPacketOk;
        }, ex -> radiusPacketException), radiusPacketException);
    }
}
