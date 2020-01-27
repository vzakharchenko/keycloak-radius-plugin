package com.github.vzakharchenko.radius.dm.logout;

import com.github.vzakharchenko.radius.coa.RadiusCoAClientHelper;
import com.github.vzakharchenko.radius.configuration.RadiusConfigHelper;
import com.github.vzakharchenko.radius.dm.jpa.DisconnectMessageManager;
import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModel;
import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModelBuilder;
import com.github.vzakharchenko.radius.event.log.EventLoggerUtils;
import com.github.vzakharchenko.radius.providers.IRadiusCOAProvider;
import com.github.vzakharchenko.radius.providers.IRadiusCOAProviderFactory;
import com.github.vzakharchenko.radius.radius.RadiusLibraryUtils;
import com.github.vzakharchenko.radius.radius.handlers.session.KeycloakSessionUtils;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfo;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusEndpoint;

import java.net.InetSocketAddress;

import static org.keycloak.events.EventType.LOGOUT_ERROR;
import static org.tinyradius.packet.PacketType.DISCONNECT_ACK;
import static org.tinyradius.packet.PacketType.DISCONNECT_REQUEST;
import static org.tinyradius.packet.RadiusPackets.nextPacketId;

public class RadiusLogout implements IRadiusCOAProvider,
        IRadiusCOAProviderFactory<IRadiusCOAProvider> {

    public static final String RADIUS_LOGOUT_FACTORY = "radius-logout-factory";

    @Override
    public IRadiusCOAProvider create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

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
        dmPacket.addAttribute("Acct-Session-Id", dm.getId());
        dmPacket.addAttribute("NAS-IP-Address", dm.getNasIp());
        dmPacket.addAttribute("Calling-Station-Id", dm.getCallingStationId());
        RadiusLibraryUtils.setUserName(dmPacket, dm.getUserName());
        if (dm.getNasPort() != null) {
            dmPacket.addAttribute("NAS-Port", dm.getNasPort());
        }
        dmPacket.addAttribute("NAS-Port-Type", dm.getNasPort() != null ?
                dm.getNasPortType() : "Unknown");

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
        EventLoggerUtils.createEvent(session,
                radiusUserInfo.getRealmModel(),
                radiusUserInfo.getClientModel(),
                radiusUserInfo.getClientConnection())
                .user(radiusUserInfo.getUserModel()).event(LOGOUT_ERROR)
                .error("Radius Logout Fail" +
                        answer.getAttributeValue("Error-Cause"));
    }

    private DisconnectMessageModel getDisconnectMessageModel(AccountingRequest request,
                                                             KeycloakSession session) {
        DisconnectMessageManager disconnectMessageManager =
                new DisconnectMessageManager(session);
        return disconnectMessageManager.
                getDisconnectMessage(request.getUserName(),
                        getAttr("Acct-Session-Id", request));
    }

    protected void endSession(KeycloakSession session, DisconnectMessageModel dm) {
        DisconnectMessageManager disconnectMessageManager =
                new DisconnectMessageManager(session);
        disconnectMessageManager.endSession(dm);
    }

    protected void answerHandler(RadiusPacket answer,
                                 KeycloakSession session,
                                 DisconnectMessageModel dm) {
        if (answer.getType() == DISCONNECT_ACK) {
            endSession(session, dm);
        } else {
            sendErrorEvent(session, answer);
        }
    }

    @Override
    public void logout(AccountingRequest request, KeycloakSession session) {
        IRadiusUserInfo radiusUserInfo = KeycloakSessionUtils
                .getRadiusSessionInfo(session);
        DisconnectMessageModel dm = getDisconnectMessageModel(request, session);
        if (dm != null) {
            RadiusCoAClientHelper.requestCoA(request.getDictionary(), radiusClient -> {
                RadiusPacket radiusPacket = new RadiusPacket(request.getDictionary(),
                        DISCONNECT_REQUEST, nextPacketId());
                prepareDisconnectMessagePacket(radiusPacket, dm);
                RadiusPacket answer = radiusClient.communicate(radiusPacket,
                        getRadiusEndpoint(radiusUserInfo)).syncUninterruptibly().getNow();
                answerHandler(answer, session, dm);
            });
        }
    }
}
