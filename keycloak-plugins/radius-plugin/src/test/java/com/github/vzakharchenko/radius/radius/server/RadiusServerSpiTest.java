package com.github.vzakharchenko.radius.radius.server;

import com.github.vzakharchenko.radius.providers.IRadiusServerProvider;
import com.github.vzakharchenko.radius.providers.IRadiusServerProviderFactory;
import com.github.vzakharchenko.radius.providers.RadiusServerSpi;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class RadiusServerSpiTest {
    private final RadiusServerSpi radiusServerSpi = new RadiusServerSpi();

    @Test
    public void testMethods() {
        assertEquals(radiusServerSpi.getName(), RadiusServerSpi.RADIUS_SPI);
        assertFalse(radiusServerSpi.isInternal());
        assertEquals(radiusServerSpi.getProviderClass(), IRadiusServerProvider.class);
        assertEquals(radiusServerSpi.getProviderFactoryClass(), IRadiusServerProviderFactory.class);
    }
}
