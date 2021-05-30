package com.github.vzakharchenko.radius.providers;

import org.testng.annotations.Test;

import static com.github.vzakharchenko.radius.providers.AccountingRadiusHandlerSPI.ACCOUNT_RADIUS_SPI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class AccountRadiusHandlerSpiTest {
    private final AccountingRadiusHandlerSPI radiusProviderSpi =
            new AccountingRadiusHandlerSPI();

    @Test
    public void testMethods() {
        assertEquals(radiusProviderSpi.getProviderClass(),
                IRadiusAccountHandlerProvider.class);
        assertEquals(radiusProviderSpi.getProviderFactoryClass(),
                IRadiusAccountHandlerProviderFactory.class);
        assertEquals(radiusProviderSpi.getName(), ACCOUNT_RADIUS_SPI);
        assertFalse(radiusProviderSpi.isInternal());
    }
}
