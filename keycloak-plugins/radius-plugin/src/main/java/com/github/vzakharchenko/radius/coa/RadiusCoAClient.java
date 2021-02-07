package com.github.vzakharchenko.radius.coa;

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

import java.net.InetSocketAddress;

public class RadiusCoAClient implements IRadiusCoAClient {

    private static final NioEventLoopGroup EVENT_EXECUTORS = new NioEventLoopGroup(4);
    private static final Bootstrap BOOTSTRAP = new Bootstrap().group(EVENT_EXECUTORS)
            .channel(NioDatagramChannel.class);

    @Override
    public void requestCoA(Dictionary dictionary, ICoaRequestHandler coaRequestHandler) {
        final Timer timer = new HashedWheelTimer();
        final PacketEncoder packetEncoder = new PacketEncoder(dictionary);
        try (RadiusClient rc = new RadiusClient(
                BOOTSTRAP, new InetSocketAddress(0),
                new BasicTimeoutHandler(timer, 3, 3000),
                new CoAChannelInitializer(new ClientPacketCodec(packetEncoder)))) {
            coaRequestHandler.call(rc);
        }
    }
}
