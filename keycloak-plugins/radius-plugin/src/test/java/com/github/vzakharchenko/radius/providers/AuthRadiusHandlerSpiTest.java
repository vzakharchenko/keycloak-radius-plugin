package com.github.vzakharchenko.radius.providers;

import org.testng.annotations.Test;

import static com.github.vzakharchenko.radius.providers.AuthRadiusHandlerSPI.AUTH_RADIUS_SPI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class AuthRadiusHandlerSpiTest {
    private final AuthRadiusHandlerSPI radiusProviderSpi =
            new AuthRadiusHandlerSPI();

    @Test
    public void testMethods() {
        assertEquals(radiusProviderSpi.getProviderClass(),
                IRadiusAuthHandlerProvider.class);
        assertEquals(radiusProviderSpi.getProviderFactoryClass(),
                IRadiusAuthHandlerProviderFactory.class);
        assertEquals(radiusProviderSpi.getName(), AUTH_RADIUS_SPI);
        assertFalse(radiusProviderSpi.isInternal());
    }
}
