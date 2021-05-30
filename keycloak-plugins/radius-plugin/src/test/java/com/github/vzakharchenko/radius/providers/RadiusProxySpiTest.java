package com.github.vzakharchenko.radius.providers;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class RadiusProxySpiTest {
    private final RadiusProxySpi radiusProviderSpi = new RadiusProxySpi();

    @Test
    public void testMethods() {
        assertEquals(radiusProviderSpi.getProviderClass(),
                IRadiusProxyProvider.class);
        assertEquals(radiusProviderSpi.getProviderFactoryClass(),
                IRadiusProxyProviderFactory.class);
        assertEquals(radiusProviderSpi.getName(), "radius-proxy-spi");
        assertFalse(radiusProviderSpi.isInternal());
    }
}
