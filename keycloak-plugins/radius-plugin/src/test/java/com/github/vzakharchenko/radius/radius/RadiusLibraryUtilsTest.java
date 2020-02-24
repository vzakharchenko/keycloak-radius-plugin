package com.github.vzakharchenko.radius.radius;

import com.github.vzakharchenko.radius.models.file.RadiusAccessModel;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RadiusLibraryUtilsTest {
    @Test
    public void testgetOrEmpty() {
        byte[] bytes = RadiusLibraryUtils.getOrEmpty(null, 16);
        assertEquals(bytes, new byte[16]);
        byte[] bytes1 = {1};
        bytes = RadiusLibraryUtils.getOrEmpty(bytes1, 1);
        assertEquals(bytes, bytes1);
    }

    @Test
    public void writeValueAsString() {
        assertNotNull(RadiusLibraryUtils.writeValueAsString(new RadiusAccessModel()));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void writeValueAsStringException() {
        RadiusLibraryUtils.writeValueAsString(new RadiusAccessModel() {
            @Override
            public String getSharedSecret() {
                throw new IllegalStateException("test");
            }
        });
    }
}
