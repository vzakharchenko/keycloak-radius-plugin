package com.github.vzakharchenko.radius.coa;

import com.github.vzakharchenko.radius.providers.IRadiusCOAProvider;
import com.github.vzakharchenko.radius.providers.IRadiusCOAProviderFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import org.keycloak.Config;
import org.keycloak.models.AuthenticatedClientSessionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
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


    @Override
    public void initSession(AccountingRequest accountingRequest) {

    }

    /**
     * Frame 7: 165 bytes on wire (1320 bits), 165 bytes captured (1320 bits) on interface 0
     * Ethernet II, Src: Routerbo_35:74:7b (b8:69:f4:35:74:7b), Dst: Apple_d2:cc:ac (34:36:3b:d2:cc:ac)
     * Internet Protocol Version 4, Src: 192.100.200.1, Dst: 192.100.200.208
     * User Datagram Protocol, Src Port: 53084, Dst Port: 37008
     * TZSP: Ethernet
     * Ethernet II, Src: 74:4d:28:e6:87:10 (74:4d:28:e6:87:10), Dst: Routerbo_35:74:7b (b8:69:f4:35:74:7b)
     * Internet Protocol Version 4, Src: 192.100.200.2, Dst: 192.100.200.1
     * User Datagram Protocol, Src Port: 50456, Dst Port: 3799
     * RADIUS Protocol
     *     Code: Disconnect-Request (40)
     *     Packet identifier: 0x6 (6)
     *     Length: 76
     *     Authenticator: 5ac26e578725f6b2ab2419462a4335e0
     *     [The response to this request is in frame 9]
     *     Attribute Value Pairs
     *         AVP: t=User-Name(1) l=6 val=test
     *         AVP: t=NAS-Port(5) l=6 val=15728651
     *         AVP: t=NAS-Port-Type(61) l=6 val=Virtual(5)
     *         AVP: t=NAS-IP-Address(4) l=6 val=192.100.200.1
     *         AVP: t=Framed-IP-Address(8) l=6 val=192.100.200.39
     *         AVP: t=Calling-Station-Id(31) l=16 val=192.100.200.16
     *         AVP: t=Acct-Session-Id(44) l=10 val=81100009
     * @param accountingRequest
     */
    @Override
    public void logout(AccountingRequest accountingRequest) {
        final Timer timer = new HashedWheelTimer();
        final NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
        final PacketEncoder packetEncoder = new PacketEncoder(accountingRequest.getDictionary());
        final Bootstrap bootstrap = new Bootstrap().group(eventLoopGroup).channel(NioDatagramChannel.class);

        RadiusClient rc = new RadiusClient(
                bootstrap, new InetSocketAddress(0), new BasicTimeoutHandler(timer), new ChannelInitializer<DatagramChannel>() {
            @Override
            protected void initChannel(DatagramChannel ch) {
                ch.pipeline().addLast(new ClientPacketCodec(packetEncoder), new PromiseAdapter());
            }
        });
        try {
            final RadiusEndpoint authEndpoint = new RadiusEndpoint(new InetSocketAddress("192.100.200.1", 3799), "test");
            RadiusPacket radiusPacket = new RadiusPacket(accountingRequest.getDictionary(), DISCONNECT_REQUEST, nextPacketId());
            radiusPacket.addAttribute("NAS-Port", csm.getNote("NAS-Port"));
        } finally {
            rc.close();
        }


    }
}
