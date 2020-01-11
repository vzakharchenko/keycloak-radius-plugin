package ua.zaskarius.keycloak.plugins.radius.providers;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class RadiusDictionarySpiTest {
    private RadiusDictionarySpi radiusProviderSpi = new RadiusDictionarySpi();

    @Test
    public void testMethods() {
        assertEquals(radiusProviderSpi.getProviderClass(),
                IRadiusDictionaryProvider.class);
        assertEquals(radiusProviderSpi.getProviderFactoryClass(),
                IRadiusDictionaryProviderFactory.class);
        assertEquals(radiusProviderSpi.getName(), RadiusDictionarySpi.RADIUS_DICTIONARY);
        assertFalse(radiusProviderSpi.isInternal());
    }
}
