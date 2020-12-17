package com.github.vzakharchenko.radius.radius.handlers.session;

import com.github.vzakharchenko.radius.mappers.IRadiusSessionPasswordManager;
import com.github.vzakharchenko.radius.mappers.RadiusSessionPasswordManager;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import static com.github.vzakharchenko.radius.mappers.RadiusSessionPasswordManager.RADIUS_SESSION_EXPIRATION;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class RadiusSessionPasswordManagerTest extends AbstractRadiusTest {
    IRadiusSessionPasswordManager radiusSessionPasswordManager = RadiusSessionPasswordManager.getInstance();

    @Test
    public void testGetPassword() {
        String currentPassword = radiusSessionPasswordManager.getCurrentPassword(userSessionModel);
        assertEquals(currentPassword, "123");
    }

    @Test
    public void testGetExpirationPassword() {
        String currentPassword = radiusSessionPasswordManager.getCurrentPassword(userSessionModel);
        assertEquals(currentPassword, "123");
        when(userSessionModel.getNote(RADIUS_SESSION_EXPIRATION)).thenReturn("0");
        assertNull(radiusSessionPasswordManager.getCurrentPassword(userSessionModel));
    }

    @Test
    public void testGetExpirationPasswordNull() {
        String currentPassword = radiusSessionPasswordManager.getCurrentPassword(userSessionModel);
        assertEquals(currentPassword, "123");
        when(userSessionModel.getNote(RADIUS_SESSION_EXPIRATION)).thenReturn(null);
        assertNull(radiusSessionPasswordManager.getCurrentPassword(userSessionModel));
    }

}
