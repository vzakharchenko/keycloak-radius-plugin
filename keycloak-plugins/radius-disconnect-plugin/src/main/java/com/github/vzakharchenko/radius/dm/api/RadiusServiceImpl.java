package com.github.vzakharchenko.radius.dm.api;

import com.github.vzakharchenko.radius.client.RadiusLoginProtocolFactory;
import com.github.vzakharchenko.radius.configuration.RadiusConfigHelper;
import com.github.vzakharchenko.radius.dm.jpa.DisconnectMessageManager;
import com.github.vzakharchenko.radius.dm.jpa.DmTableManager;
import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModel;
import com.github.vzakharchenko.radius.dm.models.RadiusInfoModel;
import com.github.vzakharchenko.radius.dm.models.RadiusServiceModel;
import com.github.vzakharchenko.radius.models.RadiusServerSettings;
import com.google.common.annotations.VisibleForTesting;
import jakarta.ws.rs.ForbiddenException;
import org.keycloak.Config;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.resource.RealmResourceProviderFactory;

import jakarta.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RadiusServiceImpl implements RealmResourceProviderFactory,
        RadiusService {

    public static final String RADIUS_SERVICE = "radius";
    public static final String RADIUS_SESSION_ROLE = "Radius Session Role";

    private KeycloakSession session;

    private DmTableManager tableManager;

    private KeycloakStaticHelper keycloakStaticHelper;

    @Override
    public RadiusService create(KeycloakSession keycloakSession) {
        this.session = keycloakSession;
        this.tableManager = new DisconnectMessageManager(keycloakSession);
        this.keycloakStaticHelper = new KeycloakStaticHelperImpl();
        return this;
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        factory.register(event -> {
            if (event instanceof ClientModel.ClientCreationEvent) {
                ClientModel.ClientCreationEvent postCreateEvent =
                        (ClientModel.ClientCreationEvent) event;
                KeycloakModelUtils.runJobInTransaction(factory,
                        keycloakSession ->
                                create(keycloakSession)
                                        .init(postCreateEvent.getCreatedClient()));

            }
            if (event instanceof ClientModel.ClientUpdatedEvent) {
                ClientModel.ClientUpdatedEvent postCreateEvent =
                        (ClientModel.ClientUpdatedEvent) event;
                KeycloakModelUtils.runJobInTransaction(factory,
                        keycloakSession ->
                                create(keycloakSession).init(postCreateEvent.getUpdatedClient()));
            }
        });
    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return RADIUS_SERVICE;
    }

    @Override
    public RadiusService getResource() {
        return this;
    }

    @Override
    public void init(ClientModel clientModel) {
        if (RadiusLoginProtocolFactory.RADIUS_PROTOCOL
                .equalsIgnoreCase(clientModel.getProtocol())) {
            RoleModel role = session.roles().getRealmRole(
                    clientModel.getRealm(),
                    RADIUS_SESSION_ROLE);
            if (role == null) {
                session.roles().addRealmRole(clientModel.getRealm(), RADIUS_SESSION_ROLE);
            }
        }
    }

    protected void checkToken() {
        AccessToken accessToken = keycloakStaticHelper.getAccessToken(session);
        if (accessToken == null) {
            throw new ForbiddenException("UnAuthorized");
        }
        if (!accessToken.getRealmAccess().isUserInRole(RADIUS_SESSION_ROLE)) {
            throw new ForbiddenException("UnAuthorized");
        }
        if (session.getContext().getRealm().getClientsStream().noneMatch(
                clientModel -> RadiusLoginProtocolFactory.RADIUS_PROTOCOL
                        .equalsIgnoreCase(clientModel.getProtocol())
                        && clientModel.isEnabled())) {
            throw new ForbiddenException("UnAuthorized");
        }
    }


    private void transform(RadiusServiceModel rsm,
                           DisconnectMessageModel dmm) {

        rsm.setAddress(dmm.getAddress());
        rsm.setCallingStationId(dmm.getCallingStationId());
        rsm.setCalledStationId(dmm.getCalledStationId());
        rsm.setClientId(dmm.getClientId());
        rsm.setNasIp(dmm.getNasIp());
        rsm.setUserName(dmm.getUserName());
        rsm.setFramedIp(dmm.getFramedIp());
        rsm.setSessionId(dmm.getKeycloakSessionId());
    }

    private RadiusServiceModel transform(DisconnectMessageModel dmm) {
        String userId = dmm.getUserId();
        String realmId = dmm.getRealmId();
        RealmModel realm = session.realms().getRealm(realmId);
        UserModel userModel = session.users().getUserById(realm, userId);
        RadiusServiceModel rsm = new RadiusServiceModel();
        transform(rsm, dmm);
        List<String> roles = new ArrayList<>(userModel
                .getRoleMappingsStream().map(RoleModel::getName)
                .collect(Collectors.toList()));
        realm.getClientsStream().forEach(app -> roles
                .addAll(userModel.getClientRoleMappingsStream(app)
                        .map(RoleModel::getName)
                        .collect(Collectors.toList())));
        rsm.setRoles(roles);
        rsm.setGroups(userModel.getGroupsStream().map(GroupModel::getName)
                .collect(Collectors.toList()));
        return rsm;
    }

    @Override
    public RadiusServiceModel getActiveUser(@QueryParam("ip") String ip,
                                            @QueryParam("calledStationId") String calledStationId) {
        // Only Users(or service accounts) with Role "Radius Session Role" has access
        checkToken();
        DisconnectMessageModel dmm =
                tableManager.getActiveSession(session.getContext()
                        .getRealm().getId(), ip, calledStationId);
        if (dmm == null) {
            return new RadiusServiceModel();
        }
        return transform(dmm);
    }

    @Override
    public RadiusServiceModel logout(@QueryParam("ip") String ip,
                                     @QueryParam("calledStationId") String calledStationId) {
        // Only Users(or service accounts) with Role "Radius Session Role" has access
        checkToken();
        RadiusServiceModel rsm = getActiveUser(ip, calledStationId);
        if (rsm.getFramedIp() != null) {
            UserSessionModel userSession = session.sessions()
                    .getUserSession(session.getContext().getRealm(), rsm.getSessionId());
            if (userSession != null) {
                session.sessions().removeUserSession(session.getContext().getRealm(), userSession);
            }
        }
        return rsm;
    }

    @Override
    public RadiusInfoModel getRadiusInfo(
            @QueryParam("calledStationId") String calledStationId) {
        // Only Users(or service accounts) with Role "Radius Session Role" has access
        checkToken();
        RadiusInfoModel radiusInfoModel = new RadiusInfoModel();
        RadiusServerSettings radiusSettings = RadiusConfigHelper.getConfig().getRadiusSettings();
        radiusInfoModel.setUdpRadius(radiusSettings.isUseUdpRadius());
        radiusInfoModel.setRadsec(radiusSettings.getRadSecSettings().isUseRadSec());
        radiusInfoModel.setCoaPort(radiusSettings.getCoASettings().getCoaPort());
        radiusInfoModel.setUseCoA(radiusSettings.getCoASettings().isUseCoAPackage());
        radiusInfoModel.setSecret(radiusSettings.getSecret());
        List<DisconnectMessageModel> sessions = tableManager
                .getAllActiveSessions(session.getContext()
                        .getRealm().getId(), calledStationId);
        radiusInfoModel.setActiveSessions(sessions
                .stream().map(this::transform)
                .collect(Collectors.toList()));
        return radiusInfoModel;
    }

    @VisibleForTesting
    public void setTableManager(DmTableManager tableManager) {
        this.tableManager = tableManager;
    }

    @VisibleForTesting
    public void setKeycloakStaticHelper(KeycloakStaticHelper keycloakStaticHelper) {
        this.keycloakStaticHelper = keycloakStaticHelper;
    }
}
