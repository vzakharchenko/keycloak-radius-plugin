package com.github.vzakharchenko.radius.proxy.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import org.tinyradius.client.RadiusClient;
import org.tinyradius.client.handler.ClientPacketCodec;
import org.tinyradius.client.timeout.BasicTimeoutHandler;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.PacketEncoder;
import org.tinyradius.packet.RadiusPacket;

import java.net.InetSocketAddress;

public class RadiusProxyClient implements IRadiusProxyClient {
    private final NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private final Bootstrap bootstrap = new Bootstrap().group(eventLoopGroup)
            .channel(NioDatagramChannel.class);
    @Override
    public RadiusPacket requestProxy(Dictionary dictionary,
                                     IProxyRequestHandler coaRequestHandler) {
        final Timer timer = new HashedWheelTimer();

        final PacketEncoder packetEncoder = new PacketEncoder(dictionary);

        try (RadiusClient rc = new RadiusClient(
                bootstrap, new InetSocketAddress(0),
                new BasicTimeoutHandler(timer, 3, 3000),
                new ProxyChannelInitializer(new ClientPacketCodec(packetEncoder)))) {
            return coaRequestHandler.call(rc);
        }
    }
}
