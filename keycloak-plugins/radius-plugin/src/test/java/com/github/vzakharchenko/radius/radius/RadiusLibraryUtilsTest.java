package com.github.vzakharchenko.radius.radius;

import com.github.vzakharchenko.radius.models.file.RadiusAccessModel;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.keycloak.models.UserModel;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class RadiusLibraryUtilsTest extends AbstractRadiusTest {
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
    @Test
    public void getByUserName() {
        when(userProvider.getUserByEmail(USER, realmModel)).thenReturn(null);
        UserModel userModel = RadiusLibraryUtils.getUserModel(session, USER, realmModel);
        assertNotNull(userModel);
        UserModel userModel2 = RadiusLibraryUtils.getUserByUsername(session, USER, realmModel);
        assertNotNull(userModel2);
        UserModel userModel3 = RadiusLibraryUtils.getUserByEmail(session, USER, realmModel);
        assertNull(userModel3);
    }
    @Test
    public void getByEmail() {
        when(userProvider.getUserByUsername(USER, realmModel)).thenReturn(null);
        UserModel userModel = RadiusLibraryUtils.getUserModel(session, USER, realmModel);
        assertNotNull(userModel);
        UserModel userModel2 = RadiusLibraryUtils.getUserByUsername(session, USER, realmModel);
        assertNull(userModel2);
        UserModel userModel3 = RadiusLibraryUtils.getUserByEmail(session, USER, realmModel);
        assertNotNull(userModel3);
    }
}
