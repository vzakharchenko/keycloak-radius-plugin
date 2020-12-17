package com.github.vzakharchenko.radius.radius.handlers.protocols;

import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class VendorUtilsTest {
    @Test
    public void testVendorUtils() {
        assertNotNull(VendorUtils.MIKROTIK_REALM);
    }
}
