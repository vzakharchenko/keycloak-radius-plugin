package ua.zaskarius.keycloak.plugins.radius.providers;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class RadiusProviderSpiTest {
    private RadiusProviderSpi radiusProviderSpi = new RadiusProviderSpi();

    @Test
    public void testMethods() {
        assertEquals(radiusProviderSpi.getProviderClass(),
                IRadiusConnectionProvider.class);
        assertEquals(radiusProviderSpi.getProviderFactoryClass(),
                IRadiusProviderFactory.class);
        assertEquals(radiusProviderSpi.getName(), RadiusProviderSpi.RADIUS_SPI);
        assertFalse(radiusProviderSpi.isInternal());
    }
}
