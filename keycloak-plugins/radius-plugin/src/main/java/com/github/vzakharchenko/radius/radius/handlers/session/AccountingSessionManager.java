package com.github.vzakharchenko.radius.radius.handlers.session;

import com.github.vzakharchenko.radius.RadiusHelper;
import com.github.vzakharchenko.radius.configuration.RadiusConfigHelper;
import com.github.vzakharchenko.radius.models.CoASettings;
import com.github.vzakharchenko.radius.providers.IRadiusCOAProvider;
import com.github.vzakharchenko.radius.radius.RadiusLibraryUtils;
import com.github.vzakharchenko.radius.radius.handlers.clientconnection.RadiusClientConnection;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfo;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoBuilder;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoGetter;
import com.github.vzakharchenko.radius.radius.holder.RadiusUserInfoBuilder;
import org.keycloak.common.ClientConnection;
import org.keycloak.common.util.Time;
import org.keycloak.models.*;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.util.RadiusEndpoint;

import java.util.Objects;

import static com.github.vzakharchenko.radius.radius.handlers.session.RadiusAccountState.UNSUPPORTED;

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
    private boolean isLogout;
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
        RealmModel realm = RadiusHelper.getRealm(session, accountingRequest);
        radiusUserInfoBuilder.realmModel(realm);
        userName = RadiusLibraryUtils.getUserName(accountingRequest);
        UserModel user = RadiusLibraryUtils.getUserModel(session, userName, realm);
        radiusUserInfoBuilder.userModel(user);
        radiusUserInfoBuilder.addPassword(RadiusLibraryUtils
                .getServiceAccountPassword(user, realm));
        ClientConnection clientConnection = new RadiusClientConnection(endpoint.getAddress(),
                accountingRequest);
        radiusUserInfoBuilder.clientConnection(clientConnection).address(endpoint.getAddress())
                .clientModel(RadiusLibraryUtils.getClient(clientConnection,
                        session, realm)).radiusSecret(endpoint.getSecret());
        this.radiusUserInfoGetter = radiusUserInfoBuilder.getRadiusUserInfoGetter();
        KeycloakSessionUtils.addRadiusUserInfo(session, radiusUserInfoGetter);
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
        return session.sessions()
            .getUserSessionsStream(radiusUserInfo.getRealmModel(), radiusUserInfo.getClientModel())
                 .filter(sm -> {
                     authClientSession = findAuthClientSession(sm, sessionId);
                     return authClientSession != null && Objects.equals(sm.getUser().getId(),
                         radiusUserInfo.getUserModel().getId());
                 }).findFirst().orElse(null);
    }

    private void initSession(String sessionId) {
        CoASettings coA = RadiusConfigHelper.getConfig().getRadiusSettings().getCoASettings();
        IRadiusCOAProvider provider = session.getProvider(IRadiusCOAProvider.class);
        if (provider != null && coA.isUseCoAPackage()) {
            provider.initSession(accountingRequest, session, authClientSession
                    .getUserSession().getId());
        }
        authClientSession.setNote(RADIUS_SESSION_ID, sessionId);
    }

    private UserSessionModel startSession(String sessionId) {
        if (sessionModel == null) {
            IRadiusUserInfo radiusUserInfo = radiusUserInfoGetter.getRadiusUserInfo();
            UserSessionProvider sessions = session.sessions();
            sessionModel = sessions
                    .createUserSession(null, radiusUserInfo.getRealmModel(),
                            radiusUserInfo.getUserModel(), userName,
                            radiusUserInfo.getClientConnection().getLocalAddr(), "radius", false,
                            null, null, UserSessionModel.SessionPersistenceState.PERSISTENT);
            authClientSession = sessions
                    .createClientSession(radiusUserInfo.getRealmModel(),
                            radiusUserInfo.getClientModel(), sessionModel);
            initSession(sessionId);
        }
        return sessionModel;
    }

    private RadiusAccountState getRadiusAccountState() {
        String accountStatusType = RadiusLibraryUtils.getAttributeValue(accountingRequest,
                ACCT_STATUS_TYPE);
        return RadiusAccountState
                .getByRadiusState(accountStatusType);
    }

    @Override
    @SuppressWarnings("PMD.UnusedAssignment") // a PMD false positive
    public IAccountingSessionManager manageSession() {
        String sessionId = RadiusLibraryUtils.getAttributeValue(accountingRequest,
                ACCT_SESSION_ID);
        RadiusAccountState radiusAccountState = getRadiusAccountState();
        sessionModel = getSession(sessionId);
        switch (radiusAccountState) {
            case START:
                sessionModel = startSession(sessionId);
                break;
            case STOP:
                removeSession();
                break;
            case UNSUPPORTED:
                break;
            default:
                updateSession();
                break;
        }
        return this;
    }

    private void removeSession() {
        if (sessionModel != null) {
            session.sessions().removeUserSession(radiusUserInfoGetter
                    .getRadiusUserInfo().getRealmModel(), sessionModel);
        }
        isLogout = true;
    }

    @Override
    public boolean isValidSession() {
        return sessionModel != null && !isLogout;
    }


    @Override
    public void logout() {
        RadiusAccountState radiusAccountState = getRadiusAccountState();
        CoASettings coA = RadiusConfigHelper.getConfig().getRadiusSettings().getCoASettings();
        if (radiusAccountState != UNSUPPORTED && coA.isUseCoAPackage()) {
            IRadiusCOAProvider provider = session.getProvider(IRadiusCOAProvider.class);
            if (provider != null) {
                provider.logout(accountingRequest, session);
            }
        }
    }

    private void updateSession() {
        if (sessionModel != null) {
            sessionModel.setLastSessionRefresh(Time.currentTime());
            authClientSession.setTimestamp(Time.currentTime());
        }
    }


}
