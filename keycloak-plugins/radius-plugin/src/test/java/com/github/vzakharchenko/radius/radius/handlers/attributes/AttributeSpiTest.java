package com.github.vzakharchenko.radius.radius.handlers.attributes;

import com.github.vzakharchenko.radius.providers.AttributeSpi;
import com.github.vzakharchenko.radius.providers.IRadiusAttributeProvider;
import com.github.vzakharchenko.radius.providers.IRadiusAttributesProviderFactory;
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
