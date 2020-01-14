package ua.zaskarius.keycloak.plugins.radius.providers;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class RadiusServiceSpiTest {
    private RadiusServiceSpi radiusProviderSpi = new RadiusServiceSpi();

    @Test
    public void testMethods() {
        assertEquals(radiusProviderSpi.getProviderClass(),
                IRadiusServiceProvider.class);
        assertEquals(radiusProviderSpi.getProviderFactoryClass(),
                IRadiusServiceProviderFactory.class);
        assertEquals(radiusProviderSpi.getName(), "radius-service-spi");
        assertFalse(radiusProviderSpi.isInternal());
    }
}
