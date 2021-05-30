package com.github.vzakharchenko.radius.providers;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class RadiusCOASpiTest {
    private final RadiusCOASpi radiusProviderSpi = new RadiusCOASpi();

    @Test
    public void testMethods() {
        assertEquals(radiusProviderSpi.getProviderClass(),
                IRadiusCOAProvider.class);
        assertEquals(radiusProviderSpi.getProviderFactoryClass(),
                IRadiusCOAProviderFactory.class);
        assertEquals(radiusProviderSpi.getName(), "radius-coa-spi");
        assertFalse(radiusProviderSpi.isInternal());
    }
}
