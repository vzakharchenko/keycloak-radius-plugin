package com.github.vzakharchenko.radsec.providers;

import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class RadSecSpiTest {
    private RadSecRadiusHandlerSPI radiusProviderSpi = new RadSecRadiusHandlerSPI();

    @Test
    public void testMethods() {
        assertEquals(radiusProviderSpi.getProviderClass(),
                IRadiusRadSecHandlerProvider.class);
        assertEquals(radiusProviderSpi.getProviderFactoryClass(),
                IRadiusRadSecHandlerProviderFactory.class);
        Assert.assertEquals(radiusProviderSpi.getName(), RadSecRadiusHandlerSPI.RAD_SEC_SPI);
        assertFalse(radiusProviderSpi.isInternal());
    }
}
