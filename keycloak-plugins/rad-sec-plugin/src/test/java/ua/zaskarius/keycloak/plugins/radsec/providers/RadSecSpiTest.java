package ua.zaskarius.keycloak.plugins.radsec.providers;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static ua.zaskarius.keycloak.plugins.radsec.providers.RadSecRadiusHandlerSPI.RAD_SEC_SPI;

public class RadSecSpiTest {
    private RadSecRadiusHandlerSPI radiusProviderSpi = new RadSecRadiusHandlerSPI();

    @Test
    public void testMethods() {
        assertEquals(radiusProviderSpi.getProviderClass(),
                IRadiusRadSecHandlerProvider.class);
        assertEquals(radiusProviderSpi.getProviderFactoryClass(),
                IRadiusRadSecHandlerProviderFactory.class);
        assertEquals(radiusProviderSpi.getName(), RAD_SEC_SPI);
        assertFalse(radiusProviderSpi.isInternal());
    }
}
