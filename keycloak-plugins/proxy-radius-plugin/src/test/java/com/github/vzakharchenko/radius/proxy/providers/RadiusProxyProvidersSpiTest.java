package com.github.vzakharchenko.radius.proxy.providers;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class RadiusProxyProvidersSpiTest {
    private RadiusProxyEndpointSpi radiusProviderSpi = new RadiusProxyEndpointSpi();

    @Test
    public void testMethods() {
        assertEquals(radiusProviderSpi.getProviderClass(),
                IRadiusProxyEndpointProvider.class);
        assertEquals(radiusProviderSpi.getProviderFactoryClass(),
                IRadiusProxyEndpointProviderFactory.class);
        assertEquals(radiusProviderSpi.getName(), "radius-proxy-endpoint-spi");
        assertFalse(radiusProviderSpi.isInternal());
    }
}
