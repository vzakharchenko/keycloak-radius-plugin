package com.github.vzakharchenko.radius.dm.logout;

import com.github.vzakharchenko.radius.dm.jpa.DisconnectMessageManager;
import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModel;
import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModelBuilder;
import com.github.vzakharchenko.radius.providers.IRadiusCOAProvider;
import com.github.vzakharchenko.radius.providers.IRadiusCOAProviderFactory;
import com.github.vzakharchenko.radius.radius.RadiusLibraryUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import org.keycloak.Config;
import org.keycloak.common.ClientConnection;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.tinyradius.client.RadiusClient;
import org.tinyradius.client.handler.ClientPacketCodec;
import org.tinyradius.client.handler.PromiseAdapter;
import org.tinyradius.client.timeout.BasicTimeoutHandler;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.PacketEncoder;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusEndpoint;

import java.net.InetSocketAddress;

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
    public void initSession(AccountingRequest request, KeycloakSession session,
                            ClientModel client) {
        RealmModel realm = RadiusLibraryUtils.getRealm(session, request);
        ClientConnection connection = session.getContext().getConnection();
        DisconnectMessageModel dm = DisconnectMessageModelBuilder.create()
                .id(getAttr("Acct-Session-Id", request))
                .address(connection.getRemoteAddr()).realmId(realm.getId())
                .userId(RadiusLibraryUtils.getUserModel(session, request.getUserName(), realm)
                        .getId())
                .clientId(client.getId())
                .userName(request.getUserName()).nasPort(getAttr("NAS-Port", request))
                .nasPortType(getAttr("NAS-Port-Type", request))
                .nasIp(getAttr("NAS-IP-Address", request))
                .framedIp(getAttr("Framed-IP-Address", request))
                .callingStationId(getAttr("Calling-Station-Id", request)).build();
        new DisconnectMessageManager(session).saveRadiusSession(dm);
    }

    private void prepareDisconnectMessagePacket(RadiusPacket dmPacket,
                                                DisconnectMessageModel dm) {
        dmPacket.addAttribute("Acct-Session-Id", dm.getId());
        dmPacket.addAttribute("NAS-IP-Address", dm.getNasIp());
        dmPacket.addAttribute("Calling-Station-Id", dm.getCallingStationId());
        RadiusLibraryUtils.setUserName(dmPacket, dm.getUserName());
        if (dm.getNasPort() != null) {
            dmPacket.addAttribute("NAS-Port", dm.getNasPort());
        }
        if (dm.getNasPort() != null) {
            dmPacket.addAttribute("NAS-Port-Type", dm.getNasPortType());
        }
        if (dm.getFramedIp() != null) {
            dmPacket.addAttribute("Framed-IP-Address", dm.getFramedIp());
        }
    }

    @Override
    public void logout(AccountingRequest request, KeycloakSession session) {
        DisconnectMessageModel dm = new DisconnectMessageManager(session).getDisconnectMessage(request.getUserName(), getAttr("Acct-Session-Id", request));
        if (dm != null) {
            final Timer timer = new HashedWheelTimer();
            final NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
            final PacketEncoder packetEncoder = new PacketEncoder(request.getDictionary());
            final Bootstrap bootstrap = new Bootstrap().group(eventLoopGroup).channel(NioDatagramChannel.class);

            RadiusClient rc = new RadiusClient(
                    bootstrap, new InetSocketAddress(0), new BasicTimeoutHandler(timer), new ChannelInitializer<DatagramChannel>() {
                @Override
                protected void initChannel(DatagramChannel ch) {
                    ch.pipeline().addLast(new ClientPacketCodec(packetEncoder), new PromiseAdapter());
                }
            });
            try {
                final RadiusEndpoint coaEndPoint = new RadiusEndpoint(new InetSocketAddress("192.100.200.1", 3799), "test");
                RadiusPacket radiusPacket = new RadiusPacket(request.getDictionary(), DISCONNECT_REQUEST, nextPacketId());
                prepareDisconnectMessagePacket(radiusPacket, dm);
                rc.communicate(radiusPacket, coaEndPoint).syncUninterruptibly().getNow();
            } finally {
                rc.close();
            }
        }


    }
}
