package com.github.vzakharchenko.radius.dm.logout;

import com.github.vzakharchenko.radius.coa.ICoAExceptionHandler;
import com.github.vzakharchenko.radius.coa.RadiusCoAClientHelper;
import com.github.vzakharchenko.radius.configuration.RadiusConfigHelper;
import com.github.vzakharchenko.radius.dm.jpa.DisconnectMessageManager;
import com.github.vzakharchenko.radius.dm.jpa.DmTableManager;
import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModel;
import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModelBuilder;
import com.github.vzakharchenko.radius.event.log.EventLoggerUtils;
import com.github.vzakharchenko.radius.providers.IRadiusCOAProvider;
import com.github.vzakharchenko.radius.providers.IRadiusCOAProviderFactory;
import com.github.vzakharchenko.radius.radius.RadiusLibraryUtils;
import com.github.vzakharchenko.radius.radius.dictionary.DictionaryLoader;
import com.github.vzakharchenko.radius.radius.handlers.session.KeycloakSessionUtils;
import com.github.vzakharchenko.radius.radius.handlers.session.RadiusAccountState;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfo;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.services.scheduled.ClusterAwareScheduledTaskRunner;
import org.keycloak.timer.TimerProvider;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusEndpoint;

import java.net.InetSocketAddress;
import java.util.List;

import static com.github.vzakharchenko.radius.radius.handlers.session.AccountingSessionManager.ACCT_STATUS_TYPE;
import static org.keycloak.events.EventType.LOGOUT_ERROR;
import static org.tinyradius.packet.PacketType.*;
import static org.tinyradius.packet.RadiusPackets.nextPacketId;

public class RadiusLogout implements IRadiusCOAProvider,
        IRadiusCOAProviderFactory<IRadiusCOAProvider> {

    public static final String RADIUS_LOGOUT_FACTORY = "radius-logout-factory";
    public static final String ERROR_CAUSE = "Error-Cause";
    public static final String ACCT_TERMINATE_CAUSE = "Acct-Terminate-Cause";

    private static final Logger LOGGER = Logger.getLogger(RadiusLogout.class);

    @Override
    public IRadiusCOAProvider create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {

    }


    protected void checkSessions(KeycloakSession session) {
        DmTableManager disconnectMessageManager = new DisconnectMessageManager(session);
        List<DisconnectMessageModel> sessions = disconnectMessageManager
                .getAllActivedSessions();
        for (DisconnectMessageModel dmm : sessions) {
            if (!KeycloakSessionUtils.isActiveSession(session,
                    dmm.getKeycloakSessionId(), dmm.getRealmId())) {
                requestCoA(session, dmm, new RadiusEndpoint(
                                new InetSocketAddress(dmm.getAddress(),
                                        RadiusConfigHelper.getCoASettings().getCoaPort()),
                                dmm.getSecret()),
                        ex -> disconnectMessageManager.increaseEndAttempts(dmm, ex.getMessage()));
            }
        }
    }

    private void timerSessions(KeycloakSession session) {
        TimerProvider provider = session.getProvider(TimerProvider.class);
        provider.schedule(
                new ClusterAwareScheduledTaskRunner(
                        session.getKeycloakSessionFactory(),
                        this::checkSessions,
                        60_000),
                60_000, "ClearExpiredRadiusSessions");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        KeycloakModelUtils.runJobInTransaction(factory, this::timerSessions);
    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return RADIUS_LOGOUT_FACTORY;
    }


    private String getAttr(String name, AccountingRequest request) {
        return request.getAttributeValue(name);
    }

    @Override
    public void initSession(AccountingRequest request,
                            KeycloakSession session,
                            String sessionId) {
        IRadiusUserInfo radiusUserInfo = KeycloakSessionUtils
                .getRadiusUserInfo(session).getRadiusUserInfo();
        DisconnectMessageModel dm = DisconnectMessageModelBuilder.create()
                .radiusSessionId(getAttr("Acct-Session-Id", request))
                .address(radiusUserInfo.getAddress().getHostName())
                .secret(radiusUserInfo.getRadiusSecret())
                .realmId(radiusUserInfo.getRealmModel().getId())
                .userId(radiusUserInfo.getUserModel().getId())
                .clientId(radiusUserInfo.getClientModel().getId())
                .userName(request.getUserName())
                .nasPort(getAttr("NAS-Port", request))
                .nasPortType(getAttr("NAS-Port-Type", request))
                .nasIp(getAttr("NAS-IP-Address", request))
                .framedIp(getAttr("Framed-IP-Address", request))
                .callingStationId(getAttr("Calling-Station-Id", request))
                .keycloakSessionId(sessionId).build();
        new DisconnectMessageManager(session).saveRadiusSession(dm);
    }


    protected void prepareDisconnectMessagePacket(RadiusPacket dmPacket,
                                                  DisconnectMessageModel dm) {
        dmPacket.addAttribute("Acct-Session-Id", dm.getRadiusSessionId());
        dmPacket.addAttribute("NAS-IP-Address", dm.getNasIp());
        dmPacket.addAttribute("Calling-Station-Id", dm.getCallingStationId());
        RadiusLibraryUtils.setUserName(dmPacket, dm.getUserName());
        if (dm.getNasPort() != null) {
            dmPacket.addAttribute("NAS-Port", dm.getNasPort());
        }
        if (dm.getNasPortType() != null) {
            dmPacket.addAttribute("NAS-Port-Type", dm.getNasPortType());
        }

        if (dm.getFramedIp() != null) {
            dmPacket.addAttribute("Framed-IP-Address", dm.getFramedIp());
        }
    }


    protected RadiusEndpoint getRadiusEndpoint(IRadiusUserInfo radiusUserInfo) {
        return new RadiusEndpoint(new InetSocketAddress(radiusUserInfo
                .getAddress().getAddress().getHostAddress(), RadiusConfigHelper
                .getCoASettings().getCoaPort()),
                radiusUserInfo.getRadiusSecret());
    }

    protected void sendErrorEvent(
            KeycloakSession session,
            RadiusPacket answer

    ) {
        IRadiusUserInfo radiusUserInfo = KeycloakSessionUtils
                .getRadiusSessionInfo(session);
        if (radiusUserInfo != null) {
            EventLoggerUtils.createEvent(session,
                    radiusUserInfo.getRealmModel(),
                    radiusUserInfo.getClientModel(),
                    radiusUserInfo.getClientConnection())
                    .user(radiusUserInfo.getUserModel()).event(LOGOUT_ERROR)
                    .error("Radius Logout Fail with message " +
                            answer.getAttributeValue(ERROR_CAUSE));
        }
    }

    protected void endSession(KeycloakSession session, DisconnectMessageModel dm) {
        DmTableManager disconnectMessageManager =
                new DisconnectMessageManager(session);
        disconnectMessageManager.successEndSession(dm);
    }

    protected void errorSession(KeycloakSession session,
                                DisconnectMessageModel dm, RadiusPacket answer) {
        DmTableManager disconnectMessageManager =
                new DisconnectMessageManager(session);
        disconnectMessageManager.failEndSession(dm, answer.getAttributeValue(ERROR_CAUSE));
    }

    protected void answerHandler(RadiusPacket answer,
                                 KeycloakSession session,
                                 DisconnectMessageModel dm) {
        if (answer.getType() == DISCONNECT_ACK) {
            endSession(session, dm);
        } else {
            errorSession(session, dm, answer);
            sendErrorEvent(session, answer);
        }
    }

    protected void requestCoA(KeycloakSession session,
                              DisconnectMessageModel dmm,
                              RadiusEndpoint radiusEndpoint,
                              ICoAExceptionHandler exceptionHandler) {
        Dictionary dictionary = DictionaryLoader.getInstance().loadDictionary(session);
        RadiusCoAClientHelper.requestCoA(dictionary, radiusClient -> {
            RadiusPacket radiusPacket = new RadiusPacket(dictionary,
                    DISCONNECT_REQUEST, nextPacketId());
            prepareDisconnectMessagePacket(radiusPacket, dmm);
            LOGGER.info("Send CoA request to "+radiusEndpoint.getAddress());
            RadiusPacket answer = radiusClient.communicate(radiusPacket,
                    radiusEndpoint).syncUninterruptibly().getNow();
            answerHandler(answer, session, dmm);
        }, exceptionHandler);
    }

    protected boolean isDeviceRequest(DmTableManager disconnectMessageManager,
                                      AccountingRequest request,
                                      DisconnectMessageModel dm) {
        RadiusAccountState radiusState = RadiusAccountState
                .getByRadiusState(request.getAttributeValue(ACCT_STATUS_TYPE));
        if (radiusState == RadiusAccountState.STOP) {
            disconnectMessageManager.successEndSessionWithCause(
                    dm, request.getAttributeValue(ACCT_TERMINATE_CAUSE)
            );
            return true;
        }
        return false;
    }

    @Override
    public void logout(AccountingRequest request, KeycloakSession session) {
        IRadiusUserInfo radiusUserInfo = KeycloakSessionUtils
                .getRadiusSessionInfo(session);
        DmTableManager disconnectMessageManager =
                new DisconnectMessageManager(session);
        DisconnectMessageModel dm = disconnectMessageManager.
                getDisconnectMessage(request.getUserName(),
                        getAttr("Acct-Session-Id", request));
        if (dm != null && !isDeviceRequest(disconnectMessageManager, request, dm)) {
            requestCoA(session, dm, getRadiusEndpoint(radiusUserInfo), null);
        }
    }
}
