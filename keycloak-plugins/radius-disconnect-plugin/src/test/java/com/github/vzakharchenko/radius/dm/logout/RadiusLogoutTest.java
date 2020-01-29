package com.github.vzakharchenko.radius.dm.logout;

import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModel;
import com.github.vzakharchenko.radius.dm.test.AbstractJPATest;
import org.keycloak.models.KeycloakSession;
import org.testng.annotations.Test;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusEndpoint;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static com.github.vzakharchenko.radius.dm.logout.RadiusLogout.ACCT_TERMINATE_CAUSE;
import static com.github.vzakharchenko.radius.dm.logout.RadiusLogout.RADIUS_LOGOUT_FACTORY;
import static com.github.vzakharchenko.radius.radius.handlers.session.AccountingSessionManager.ACCT_STATUS_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.tinyradius.packet.PacketType.DISCONNECT_ACK;
import static org.tinyradius.packet.PacketType.DISCONNECT_NAK;

public class RadiusLogoutTest extends AbstractJPATest {

    private RadiusLogout radiusLogout = new RadiusLogout();

    @Test
    public void methodsTests() {
        radiusLogout.close();
        radiusLogout.init(null);
        radiusLogout.postInit(keycloakSessionFactory);
        assertEquals(radiusLogout.getId(), RADIUS_LOGOUT_FACTORY);
        assertEquals(radiusLogout.create(session), radiusLogout);

    }

    @Test
    public void initSession() {
        AccountingRequest request = new AccountingRequest(realDictionary, 1, new byte[16]);
        request.setUserName(USER);
        radiusLogout.initSession(request, session, "testSession");
        verify(entityManager).persist(any());
    }

    @Test
    public void logoutNull() {
        AccountingRequest request = new AccountingRequest(realDictionary, 1, new byte[16]);
        request.setUserName(USER);
        radiusLogout.logout(request, session);
    }

    @Test
    public void logoutNotNull() {

        when(typedQuery.getResultList()).thenReturn(Arrays.asList(createDisconnectMessageModel()));
        AccountingRequest request = new AccountingRequest(realDictionary, 1, new byte[16]);
        request.setUserName(USER);
        radiusLogout.logout(request, session);
    }

    @Test
    public void logoutSkipRequest() {
        reset(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(
                createDisconnectMessageModel())).thenReturn(
                Collections.emptyList());
        AccountingRequest request = new AccountingRequest(realDictionary, 1, new byte[16]);
        request.addAttribute(ACCT_STATUS_TYPE, "02");
        request.addAttribute(ACCT_TERMINATE_CAUSE, "02");
        request.setUserName(USER);
        radiusLogout.logout(request, session);
        verify(entityManager).persist(any());
    }

    @Test
    public void checkActiveSessionsTest() {
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(createDisconnectMessageModel()));
        radiusLogout.checkSessions(session);
        verify(radiusCoAClient, never()).requestCoA(any(), any());
    }

    @Test
    public void checkInActiveSessionsTest() {
        when(userSessionProvider.getUserSession(eq(realmModel), anyString()))
                .thenReturn(null);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(createDisconnectMessageModel()));
        radiusLogout.checkSessions(session);
        verify(radiusCoAClient).requestCoA(any(), any());
    }

    @Test
    public void checkSessionsTestError() {
        when(userSessionProvider.getUserSession(eq(realmModel), anyString()))
                .thenReturn(null);
        doThrow(new IllegalStateException("test")).when(radiusCoAClient).requestCoA(any(), any());
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(createDisconnectMessageModel()), Collections.emptyList());
        radiusLogout.checkSessions(session);
        verify(entityManager).persist(any());
    }


    @Test
    public void prepareDisconnectMessagePacketTest() {
        RadiusPacket radiusPacket = new RadiusPacket(realDictionary, 40, 1);
        radiusLogout.prepareDisconnectMessagePacket(radiusPacket, createDisconnectMessageModel());
        assertEquals(radiusPacket.getAttributes().size(), 7);
    }

    @Test
    public void testGetRadiusEndpoint() {
        RadiusEndpoint radiusEndpoint = radiusLogout.getRadiusEndpoint(radiusUserInfo);
        assertNotNull(radiusEndpoint);
    }

    @Test
    public void testSendErrorEvent() {
        RadiusPacket radiusPacket = new RadiusPacket(realDictionary, 40, 1);
        radiusLogout.sendErrorEvent(session, radiusPacket);
    }

    @Test
    public void testSendErrorEventNull() {
        RadiusPacket radiusPacket = new RadiusPacket(realDictionary, 40, 1);
        radiusLogout.sendErrorEvent(mock(KeycloakSession.class), radiusPacket);
    }

    @Test
    public void testEndSession() {
        radiusLogout.endSession(session, createDisconnectMessageModel());
        verify(entityManager).persist(any());
    }

    @Test
    public void testAnswerHandler1() {
        RadiusPacket radiusPacket = new RadiusPacket(realDictionary, DISCONNECT_ACK, 1);
        radiusLogout.answerHandler(radiusPacket, session, createDisconnectMessageModel());
        verify(entityManager).persist(any());
    }

    @Test
    public void testAnswerHandler2() {
        RadiusPacket radiusPacket = new RadiusPacket(realDictionary, DISCONNECT_NAK, 1);
        radiusLogout.answerHandler(radiusPacket, session, createDisconnectMessageModel());
        verify(entityManager).persist(any());
    }

    private DisconnectMessageModel createDisconnectMessageModel() {
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        disconnectMessageModel.setKeycloakSessionId("testSession");
        disconnectMessageModel.setUserId(USER);
        disconnectMessageModel.setClientId(CLIENT_ID);
        disconnectMessageModel.setRealmId(REALM_RADIUS_NAME);
        disconnectMessageModel.setId("sessionId");
        disconnectMessageModel.setCreatedDate(new Date(10000L));
        disconnectMessageModel.setAddress("127.0.0.1");
        disconnectMessageModel.setCallingStationId("127.0.0.3");
        disconnectMessageModel.setFramedIp("127.0.0.2");
        disconnectMessageModel.setNasIp("127.0.0.4");
        disconnectMessageModel.setNasPort("01");
        disconnectMessageModel.setNasPortType("00");
        disconnectMessageModel.setUserName(USER);
        disconnectMessageModel.setSecret("set");
        disconnectMessageModel.setRadiusSessionId("sessionId");
        return disconnectMessageModel;
    }

}
