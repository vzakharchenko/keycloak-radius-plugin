package com.github.vzakharchenko.radius.dm.api;

import com.github.vzakharchenko.radius.dm.jpa.DmTableManager;
import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModel;
import com.github.vzakharchenko.radius.dm.models.RadiusInfoModel;
import com.github.vzakharchenko.radius.dm.models.RadiusServiceModel;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import jakarta.ws.rs.ForbiddenException;
import org.keycloak.Config;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RoleModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.provider.ProviderEventListener;
import org.keycloak.representations.AccessToken;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class RadiusServiceTest extends AbstractRadiusTest {

    private RadiusServiceImpl radiusService = new RadiusServiceImpl();

    @Mock
    private DmTableManager tableManager;

    @Mock
    private ClientModel.ClientCreationEvent clientCreationEvent;
    @Mock
    private ClientModel.ClientUpdatedEvent clientUpdatedEvent;
    @Mock
    private KeycloakStaticHelper keycloakStaticHelper;

    @Mock
    private RoleModel roleModel;
    @Mock
    private Stream roleModelStream;
    @Mock
    private Stream clientModelStream;

    private AccessToken accessToken;

    @BeforeMethod
    public void beforeTests() {
        reset(
                tableManager,
                clientCreationEvent,
                clientUpdatedEvent,
                roleModelStream,
                clientModelStream,
                roleModel);
        when(clientCreationEvent.getCreatedClient()).thenReturn(clientModel);
        when(clientUpdatedEvent.getUpdatedClient()).thenReturn(clientModel);
        when(clientUpdatedEvent.getKeycloakSession()).thenReturn(session);
        radiusService.create(session);
        radiusService.setKeycloakStaticHelper(keycloakStaticHelper);
        AccessToken.Access access = new AccessToken.Access();
        access.roles(new HashSet<>(Collections
                .singletonList(RadiusServiceImpl.RADIUS_SESSION_ROLE)));
        accessToken = new AccessToken();
        accessToken.setRealmAccess(access);
        when(keycloakStaticHelper.getAccessToken(session)).thenReturn(accessToken);
        radiusService.setTableManager(tableManager);

        when(userModel.getRoleMappingsStream()).thenReturn(roleModelStream);
        when(roleModelStream.map(any())).thenReturn(roleModelStream);

        when(userModel.getClientRoleMappingsStream(clientModel)).thenReturn(clientModelStream);
        when(clientModelStream.map(any())).thenReturn(roleModelStream);

        when(roleProvider
                .getRealmRole(realmModel, RadiusServiceImpl.RADIUS_SESSION_ROLE))
                .thenReturn(roleModel);
    }


    @Test
    public void testCreate() {
        RadiusService rs = this.radiusService.create(session);
        assertNotNull(rs);
    }

    @Test
    public void postInitClientCreationTest() {
        doAnswer(invocationOnMock -> {
            Object argument = invocationOnMock.getArgument(0);
            ProviderEventListener eventListener = (ProviderEventListener) argument;
            eventListener.onEvent(clientCreationEvent);
            return null;
        }).when(keycloakSessionFactory).register(any());

        this.radiusService.postInit(keycloakSessionFactory);
    }

    @Test
    public void postInitClientUpdatedTest() {
        doAnswer(invocationOnMock -> {
            Object argument = invocationOnMock.getArgument(0);
            ProviderEventListener eventListener = (ProviderEventListener) argument;
            eventListener.onEvent(clientUpdatedEvent);
            return null;
        }).when(keycloakSessionFactory).register(any());

        this.radiusService.postInit(keycloakSessionFactory);
    }

    @Test
    public void initAlreadyCreatedRealmTest() {
        this.radiusService.init(clientModel);
        verify(roleProvider, never()).addRealmRole(realmModel,
                RadiusServiceImpl.RADIUS_SESSION_ROLE);
    }

    @Test
    public void initNoRadiusClientTest() {
        when(clientModel.getProtocol()).thenReturn(OIDCLoginProtocol.LOGIN_PROTOCOL);
        when(roleProvider
                .getRealmRole(realmModel, RadiusServiceImpl.RADIUS_SESSION_ROLE))
                .thenReturn(null);
        this.radiusService.init(clientModel);
        verify(roleProvider, never())
                .addRealmRole(realmModel, RadiusServiceImpl.RADIUS_SESSION_ROLE);
    }

    @Test
    public void initCreateRealmTest() {
        when(roleProvider
                .getRealmRole(realmModel, RadiusServiceImpl.RADIUS_SESSION_ROLE))
                .thenReturn(null);
        this.radiusService.init(clientModel);
        verify(roleProvider).addRealmRole(realmModel, RadiusServiceImpl.RADIUS_SESSION_ROLE);
    }

    @Test
    public void checkTokenTest() {
        radiusService.checkToken();
    }

    @Test(expectedExceptions = ForbiddenException.class,
            expectedExceptionsMessageRegExp = "UnAuthorized")
    public void checkTokenWrongProtocolTest() {
        when(clientModel.getProtocol()).thenReturn(OIDCLoginProtocol.LOGIN_PROTOCOL);
        radiusService.checkToken();
    }

    @Test(expectedExceptions = ForbiddenException.class,
            expectedExceptionsMessageRegExp = "UnAuthorized")
    public void checkTokenDisabledClientTest() {
        when(clientModel.isEnabled()).thenReturn(false);
        radiusService.checkToken();
    }

    @Test(expectedExceptions = ForbiddenException.class,
            expectedExceptionsMessageRegExp = "UnAuthorized")
    public void checkTokenRoleTest() {
        AccessToken.Access access = new AccessToken.Access();
        access.roles(new HashSet<>(Collections.singletonList("SOME ROLE")));
        accessToken = new AccessToken();
        accessToken.setRealmAccess(access);
        when(keycloakStaticHelper.getAccessToken(session)).thenReturn(accessToken);
        radiusService.checkToken();
    }

    @Test(expectedExceptions = ForbiddenException.class,
            expectedExceptionsMessageRegExp = "UnAuthorized")
    public void checkTokenWithoutTokenTest() {
        when(keycloakStaticHelper.getAccessToken(session)).thenReturn(null);
        radiusService.checkToken();
    }

    @Test
    public void getActiveUserTest() {
        when(roleModelStream.collect(any())).thenReturn(Arrays.asList("TEST"));
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        disconnectMessageModel.setClientId("clientId");
        disconnectMessageModel.setUserId(USER);
        disconnectMessageModel.setRealmId(REALM_RADIUS_ID);
        when(tableManager.getActiveSession(any(), any(), any())).thenReturn(disconnectMessageModel);
        RadiusServiceModel activeUser = radiusService.getActiveUser("test", "test");
        assertNotNull(activeUser);
        assertEquals(activeUser.getClientId(), "clientId");
    }

    @Test
    public void getActiveUserNullTest() {
        when(roleModelStream.collect(any())).thenReturn(Arrays.asList("TEST"));
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        disconnectMessageModel.setClientId("clientId");
        disconnectMessageModel.setUserId(USER);
        disconnectMessageModel.setRealmId(REALM_RADIUS_ID);
        when(tableManager.getActiveSession(any(), any(), any())).thenReturn(null);
        RadiusServiceModel activeUser = radiusService.getActiveUser("test", "test");
        assertNotNull(activeUser);
        assertNull(activeUser.getClientId());
    }

    @Test
    public void getRadiusInfoTest() {

        when(roleModelStream.collect(any())).thenReturn(Arrays.asList("TEST"));
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        disconnectMessageModel.setClientId("clientId");
        disconnectMessageModel.setUserId(USER);
        disconnectMessageModel.setRealmId(REALM_RADIUS_ID);

        when(tableManager.getAllActiveSessions(any(), any()))
                .thenReturn(Arrays.asList(disconnectMessageModel));
        RadiusInfoModel radiusInfo = radiusService.getRadiusInfo("test");
        assertNotNull(radiusInfo);
        assertNotNull(radiusInfo.getActiveSessions());
        assertEquals(radiusInfo.getActiveSessions().size(), 1);
    }

    @Test
    public void logoutTest() {
        when(roleModelStream.collect(any())).thenReturn(Arrays.asList("TEST"));
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        disconnectMessageModel.setClientId("clientId");
        disconnectMessageModel.setKeycloakSessionId("sessionId");
        disconnectMessageModel.setUserId(USER);
        disconnectMessageModel.setRealmId(REALM_RADIUS_ID);
        disconnectMessageModel.setFramedIp("test");
        when(tableManager.getActiveSession(any(), any(), any())).thenReturn(disconnectMessageModel);
        RadiusServiceModel test = radiusService.logout("test", "test");
        assertNotNull(test);
        verify(userSessionProvider).removeUserSession(realmModel, userSessionModel);
    }

    @Test
    public void logoutWithoutSessionTest() {
        when(tableManager.getActiveSession(any(), any(), any())).thenReturn(null);
        RadiusServiceModel test = radiusService.logout("test", "test");
        assertNotNull(test);
        verify(userSessionProvider, never()).removeUserSession(realmModel, userSessionModel);
    }

    @Test
    public void logoutwithoutIpTest() {
        when(roleModelStream.collect(any())).thenReturn(Arrays.asList("TEST"));
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        disconnectMessageModel.setClientId("clientId");
        disconnectMessageModel.setKeycloakSessionId("sessionId");
        disconnectMessageModel.setUserId(USER);
        disconnectMessageModel.setRealmId(REALM_RADIUS_ID);
        disconnectMessageModel.setFramedIp(null);
        when(tableManager.getActiveSession(any(), any(), any())).thenReturn(disconnectMessageModel);
        RadiusServiceModel test = radiusService.logout("test", "test");
        assertNotNull(test);
        verify(userSessionProvider, never()).removeUserSession(realmModel, userSessionModel);
    }

    @Test
    public void logoutSessionDoesNotExistTest() {
        when(roleModelStream.collect(any())).thenReturn(Arrays.asList("TEST"));
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        disconnectMessageModel.setClientId("clientId");
        disconnectMessageModel.setKeycloakSessionId("sessionId");
        disconnectMessageModel.setUserId(USER);
        disconnectMessageModel.setRealmId(REALM_RADIUS_ID);
        disconnectMessageModel.setFramedIp("test");
        when(tableManager.getActiveSession(any(), any(), any())).thenReturn(disconnectMessageModel);
        when(userSessionProvider.getUserSession(eq(realmModel), anyString()))
                .thenReturn(null);
        RadiusServiceModel test = radiusService.logout("test", "test");
        assertNotNull(test);
        verify(userSessionProvider, never()).removeUserSession(realmModel, userSessionModel);
    }


    @Test
    public void testMethods() {
        radiusService.create(session);
        radiusService.init(new Config.SystemPropertiesScope(null));
        radiusService.close();
        assertEquals(radiusService.getId(), "radius");
        assertEquals(radiusService.getResource(), radiusService);
    }
}
