package ua.zaskarius.keycloak.plugins.radius.radius.server;

import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProviderFactory;
import ua.zaskarius.keycloak.plugins.radius.providers.RadiusServerSpi;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class RadiusServerSpiTest {
    private RadiusServerSpi radiusServerSpi = new RadiusServerSpi();

    @Test
    public void testMethods() {
        assertEquals(radiusServerSpi.getName(), RadiusServerSpi.RADIUS_SPI);
        assertFalse(radiusServerSpi.isInternal());
        assertEquals(radiusServerSpi.getProviderClass(), IRadiusServerProvider.class);
        assertEquals(radiusServerSpi.getProviderFactoryClass(), IRadiusServerProviderFactory.class);
    }
}
