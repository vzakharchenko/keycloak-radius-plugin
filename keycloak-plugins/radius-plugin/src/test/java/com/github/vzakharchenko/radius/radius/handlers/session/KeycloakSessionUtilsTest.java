package com.github.vzakharchenko.radius.radius.handlers.session;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import static com.github.vzakharchenko.radius.mappers.RadiusSessionPasswordManager.RADIUS_SESSION_PASSWORD;
import static com.github.vzakharchenko.radius.mappers.RadiusSessionPasswordManager.RADIUS_SESSION_PASSWORD_TYPE;
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
        when(session.getAttribute(anyString(), any())).thenReturn(null);
        assertNull(KeycloakSessionUtils.getRadiusSessionInfo(session));
    }

    @Test
    public void getUser() {
        assertEquals(KeycloakSessionUtils.getUser(session), userModel);
        when(session.getAttribute(anyString(), any())).thenReturn(null);
        assertNull(KeycloakSessionUtils.getUser(session));
    }

    @Test
    public void getClearOneTimePassword() {
        when(radiusUserInfo.getActivePassword()).thenReturn("123");
        KeycloakSessionUtils.clearOneTimePassword(session);
        verify(userSessionModel).removeNote(RADIUS_SESSION_PASSWORD);
    }

    @Test
    public void getClearOneTimePasswordDefault() {
        when(userSessionModel.getNote(RADIUS_SESSION_PASSWORD_TYPE))
                .thenReturn(null);
        when(radiusUserInfo.getActivePassword()).thenReturn("123");
        KeycloakSessionUtils.clearOneTimePassword(session);
        verify(userSessionModel).removeNote(RADIUS_SESSION_PASSWORD);
    }

    @Test
    public void getClearSessionPassword() {

        when(userSessionModel.getNote(RADIUS_SESSION_PASSWORD_TYPE))
                .thenReturn("false");
        when(radiusUserInfo.getActivePassword()).thenReturn("123");
        KeycloakSessionUtils.clearOneTimePassword(session);
        verify(userSessionModel, never()).removeNote(RADIUS_SESSION_PASSWORD);
    }

    @Test
    public void getClearOneTimePasswordNotDeleted() {
        when(radiusUserInfo.getActivePassword()).thenReturn("wrong");
        KeycloakSessionUtils.clearOneTimePassword(session);
        verify(userSessionModel, never()).removeNote(RADIUS_SESSION_PASSWORD);
    }
}
