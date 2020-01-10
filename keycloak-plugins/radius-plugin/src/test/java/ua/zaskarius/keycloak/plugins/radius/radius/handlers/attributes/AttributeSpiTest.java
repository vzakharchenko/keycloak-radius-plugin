package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import ua.zaskarius.keycloak.plugins.radius.providers.AttributeSpi;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAttributeProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAttributesProviderFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class AttributeSpiTest {
    AttributeSpi attributeSpi = new AttributeSpi();

    @Test
    public void testMethods() {
        assertEquals(attributeSpi.getName(), AttributeSpi.RADIUS_ATTRIBUTES_SPI);
        assertFalse(attributeSpi.isInternal());
        assertEquals(attributeSpi.getProviderClass(),
                IRadiusAttributeProvider.class);
        assertEquals(attributeSpi.getProviderFactoryClass(),
                IRadiusAttributesProviderFactory.class);
    }
}
