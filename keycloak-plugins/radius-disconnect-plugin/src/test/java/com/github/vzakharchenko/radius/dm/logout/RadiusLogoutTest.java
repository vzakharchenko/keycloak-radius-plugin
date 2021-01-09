package com.github.vzakharchenko.radius.dm.logout;

import com.github.vzakharchenko.radius.coa.ICoaRequestHandler;
import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModel;
import com.github.vzakharchenko.radius.dm.test.AbstractJPATest;
import com.github.vzakharchenko.radius.radius.dictionary.DictionaryLoader;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.Promise;
import org.keycloak.models.KeycloakSession;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.client.RadiusClient;
import org.tinyradius.client.timeout.TimeoutHandler;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.packet.RadiusPackets;
import org.tinyradius.util.RadiusEndpoint;

import java.net.InetSocketAddress;
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

    private RadiusClient radiusClient;


    private Bootstrap bootstrap;
    @Mock
    private TimeoutHandler timeoutHandler;
    @Mock
    private EventLoop eventLoop;

    @Mock
    private Promise promise;

    @Mock
    private ChannelHandler channelHandler;

    @Mock
    private ChannelFuture channelFuture;
    @Mock
    private Channel channel;


    private void initRadiusClient() {
        reset(timeoutHandler);
        reset(eventLoop);
        reset(promise);
        reset(channelHandler);
        reset(channelFuture);
        reset(channel);
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoop).channel(NioDatagramChannel.class);
        when(eventLoop.register(any(Channel.class))).thenReturn(channelFuture);
        when(eventLoop.newPromise()).thenReturn(promise);
        when(eventLoop.next()).thenReturn(eventLoop);
        when(promise.addListener(any())).thenReturn(promise);
        when(promise.syncUninterruptibly()).thenReturn(promise);
        when(channelFuture.channel()).thenReturn(channel);
        radiusClient = new RadiusClient(bootstrap, new InetSocketAddress(0), timeoutHandler, channelHandler);
    }

    @BeforeMethod
    public void beforeMethods() {
        initRadiusClient();
    }

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
    public void checkInActiveSessionsTest2() {
        when(userSessionProvider.getUserSession(eq(realmModel), anyString()))
                .thenReturn(null);
        DisconnectMessageModel disconnectMessageModel = createDisconnectMessageModel();
        disconnectMessageModel.setNasIp(null);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(disconnectMessageModel));
        radiusLogout.checkSessions(session);
        verify(radiusCoAClient).requestCoA(any(), any());
    }

    @Test
    public void checkInActiveSessionsTest3() {
        when(userSessionProvider.getUserSession(eq(realmModel), anyString()))
                .thenReturn(null);
        DisconnectMessageModel disconnectMessageModel = createDisconnectMessageModel();
        disconnectMessageModel.setNasIp("");
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(disconnectMessageModel));
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
        assertEquals(radiusPacket.getAttributes().size(), 8);
    }

    @Test
    public void prepareDisconnectMessagePacketTest2() {
        RadiusPacket radiusPacket = new RadiusPacket(realDictionary, 40, 1);

        DisconnectMessageModel disconnectMessageModel = createDisconnectMessageModel();
        disconnectMessageModel.setCallingStationId(null);
        disconnectMessageModel.setNasIp(null);
        radiusLogout.prepareDisconnectMessagePacket(radiusPacket, disconnectMessageModel);
        assertEquals(radiusPacket.getAttributes().size(), 6);
    }

    @Test
    public void testGetRadiusEndpoint() {
        RadiusEndpoint radiusEndpoint = radiusLogout
                .getRadiusEndpoint(createDisconnectMessageModel(), radiusUserInfo);
        assertNotNull(radiusEndpoint);
    }

    @Test
    public void testGetRadiusEndpoint2() {
        RadiusEndpoint radiusEndpoint = radiusLogout
                .getRadiusEndpoint(createDisconnectMessageModel(), radiusUserInfo);
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
        DisconnectMessageModel disconnectMessageModel = createDisconnectMessageModel();
        disconnectMessageModel.setNasIp(null);
        radiusLogout.answerHandler(radiusPacket, session, disconnectMessageModel);
        verify(entityManager).persist(any());
    }

    @Test
    public void requestCoATest() {
        DictionaryLoader.getInstance().setWritableDictionary(realDictionary);
        RadiusPacket radiusPacket = RadiusPackets.create(realDictionary, DISCONNECT_ACK, 1, new byte[16]);
        when(promise.getNow()).thenReturn(radiusPacket);
        doAnswer((Answer<Void>) invocationOnMock -> {
            ICoaRequestHandler coaRequestHandler = invocationOnMock.getArgument(1);
            coaRequestHandler.call(radiusClient);
            return null;
        }).when(radiusCoAClient).requestCoA(any(), any());
        radiusLogout.requestCoA(
                session, createDisconnectMessageModel(), new RadiusEndpoint(new InetSocketAddress(0), "test"), null);

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
        disconnectMessageModel.setCalledStationId("called");
        disconnectMessageModel.setRadiusSessionId("sessionId");
        return disconnectMessageModel;
    }


}
