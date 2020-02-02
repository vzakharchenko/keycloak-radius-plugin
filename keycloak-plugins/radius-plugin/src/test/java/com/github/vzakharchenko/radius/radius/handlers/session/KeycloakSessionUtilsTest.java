package com.github.vzakharchenko.radius.radius.handlers.session;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import static com.github.vzakharchenko.radius.mappers.RadiusPasswordMapper.RADIUS_SESSION_PASSWORD;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class KeycloakSessionUtilsTest extends AbstractRadiusTest {
    @Test
    public void getIsActiveSession() {
        when(userSessionProvider.getUserSession(eq(realmModel), anyString()))
                .thenReturn(null);
        assertFalse(KeycloakSessionUtils.isActiveSession(session, "test", REALM_RADIUS_NAME));
    }

    @Test
    public void getRadiusInfo() {
        assertNotNull(KeycloakSessionUtils.getRadiusUserInfo(session));
        assertNotNull(KeycloakSessionUtils.getRadiusSessionInfo(session));
        assertTrue(KeycloakSessionUtils.isActiveSession(session, "test", REALM_RADIUS_NAME));
    }

    @Test
    public void getClearOneTimePassword() {
        when(radiusUserInfo.getActivePassword()).thenReturn("123");
        KeycloakSessionUtils.clearOneTimePassword(session);
        verify(userSessionModel).removeNote(RADIUS_SESSION_PASSWORD);
    }

    @Test
    public void getClearOneTimePasswordNotDeleted() {
        when(radiusUserInfo.getActivePassword()).thenReturn("wrong");
        KeycloakSessionUtils.clearOneTimePassword(session);
        verify(userSessionModel, never()).removeNote(RADIUS_SESSION_PASSWORD);
    }
}
