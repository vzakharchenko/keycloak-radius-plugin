package com.github.vzakharchenko.radius.coa;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.DatagramChannel;
import org.tinyradius.client.handler.ClientPacketCodec;
import org.tinyradius.client.handler.PromiseAdapter;

public class CoAChannelInitializer extends ChannelInitializer<DatagramChannel> {
    private final ClientPacketCodec clientPacketCodec;

    public CoAChannelInitializer(ClientPacketCodec clientPacketCodec) {
        super();
        this.clientPacketCodec = clientPacketCodec;
    }

    @Override
    protected void initChannel(DatagramChannel ch) {
        ch.pipeline().addLast(clientPacketCodec, new PromiseAdapter());
    }
}
