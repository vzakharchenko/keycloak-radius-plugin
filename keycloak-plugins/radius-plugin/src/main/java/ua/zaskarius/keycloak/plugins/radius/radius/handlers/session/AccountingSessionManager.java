package ua.zaskarius.keycloak.plugins.radius.radius.handlers.session;

import org.keycloak.common.ClientConnection;
import org.keycloak.common.util.Time;
import org.keycloak.models.*;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.util.RadiusEndpoint;
import ua.zaskarius.keycloak.plugins.radius.radius.holder.IRadiusUserInfo;
import ua.zaskarius.keycloak.plugins.radius.radius.holder.IRadiusUserInfoBuilder;
import ua.zaskarius.keycloak.plugins.radius.radius.holder.IRadiusUserInfoGetter;
import ua.zaskarius.keycloak.plugins.radius.radius.holder.RadiusUserInfoBuilder;
import ua.zaskarius.keycloak.plugins.radius.radius.RadiusLibraryUtils;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.clientconnection.RadiusClientConnection;

import java.util.List;
import java.util.Objects;

import static ua.zaskarius.keycloak.plugins.radius.radius.handlers.session.RadiusAccountState.STOP;

public class AccountingSessionManager implements IAccountingSessionManager {

    public static final String RADIUS_SESSION_ID = "RADIUS_SESSION_ID";
    public static final String ACCT_SESSION_ID = "Acct-Session-Id";
    public static final String ACCT_STATUS_TYPE = "Acct-Status-Type";
    private final KeycloakSession session;
    private final AccountingRequest accountingRequest;
    private final RadiusEndpoint endpoint;
    private IRadiusUserInfoGetter radiusUserInfoGetter;
    private String userName;
    private UserSessionModel sessionModel;
    private AuthenticatedClientSessionModel authClientSession;

    public AccountingSessionManager(AccountingRequest accountingRequest,
                                    KeycloakSession session,
                                    RadiusEndpoint endpoint) {
        this.session = session;
        this.accountingRequest = accountingRequest;
        this.endpoint = endpoint;
    }


    @Override
    public IAccountingSessionManager init() {

        IRadiusUserInfoBuilder radiusUserInfoBuilder = RadiusUserInfoBuilder.create();
        RealmModel realm = RadiusLibraryUtils.getRealm(session, accountingRequest);
        radiusUserInfoBuilder.realmModel(realm);
        userName = RadiusLibraryUtils.getUserName(accountingRequest);
        UserModel user = RadiusLibraryUtils.getUserModel(session, userName, realm);
        radiusUserInfoBuilder.userModel(user);
        ClientConnection clientConnection = new RadiusClientConnection(endpoint.getAddress(),
                accountingRequest);
        radiusUserInfoBuilder.clientConnection(clientConnection)
                .clientModel(RadiusLibraryUtils.getClient(clientConnection,
                        session, realm));
        this.radiusUserInfoGetter = radiusUserInfoBuilder.getRadiusUserInfoGetter();
        return this;
    }

    @Override
    public IAccountingSessionManager updateContext() {
        KeycloakSessionUtils.context(session, radiusUserInfoGetter);
        return this;
    }


    private AuthenticatedClientSessionModel findAuthClientSession(
            UserSessionModel sm,
            String sessionId) {
        return sm.getAuthenticatedClientSessions().values().stream().filter(
                acsm ->
                        Objects.equals(acsm.getNote(RADIUS_SESSION_ID), sessionId))
                .findFirst().orElse(null);
    }

    private UserSessionModel getSession(String sessionId) {
        IRadiusUserInfo radiusUserInfo = radiusUserInfoGetter.getRadiusUserInfo();
        List<UserSessionModel> userSessions = session.sessions()
                .getUserSessions(radiusUserInfo.getRealmModel(),
                        radiusUserInfo.getClientModel());

        return userSessions.stream().filter(sm -> {
                    authClientSession =
                            findAuthClientSession(sm, sessionId);
                    return authClientSession != null &&
                            Objects.equals(sm.getUser().getId(),
                                    radiusUserInfo.getUserModel().getId());
                }
        ).findFirst().orElse(null);
    }

    private UserSessionModel startSession(String sessionId) {
        IRadiusUserInfo radiusUserInfo = radiusUserInfoGetter.getRadiusUserInfo();
        UserSessionProvider sessions = session.sessions();
        sessionModel = sessions.createUserSession(
                radiusUserInfo.getRealmModel(), radiusUserInfo.getUserModel(), userName,
                radiusUserInfo.getClientConnection().getLocalAddr(),
                "radius", false, null, null);
        authClientSession = sessions
                .createClientSession(radiusUserInfo.getRealmModel(),
                        radiusUserInfo.getClientModel(), sessionModel);
        authClientSession.setNote(RADIUS_SESSION_ID, sessionId);
        return sessionModel;
    }

    @Override
    public IAccountingSessionManager manageSession() {
        String accountStatusType = RadiusLibraryUtils.getAttributeValue(accountingRequest,
                ACCT_STATUS_TYPE);
        String sessionId = RadiusLibraryUtils.getAttributeValue(accountingRequest,
                ACCT_SESSION_ID);
        RadiusAccountState radiusAccountState = RadiusAccountState
                .getByRadiusState(accountStatusType);
        sessionModel = getSession(sessionId);
        if (sessionModel == null) {
            sessionModel = startSession(sessionId);
        }
        updateSession();
        if (radiusAccountState == STOP) {
            removeSession();
        }
        return this;
    }

    private void removeSession() {
        if (sessionModel != null) {
            session.sessions().removeUserSession(radiusUserInfoGetter
                    .getRadiusUserInfo().getRealmModel(), sessionModel);
        }
    }

    @Override
    public boolean isValidSession() {
        return sessionModel != null;
    }

    private void updateSession() {
        if (sessionModel != null) {
            sessionModel.setLastSessionRefresh(Time.currentTime());
            authClientSession.setTimestamp(Time.currentTime());
        }
    }


}
