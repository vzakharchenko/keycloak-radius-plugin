package com.github.vzakharchenko.radius.proxy.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.DatagramChannel;
import org.tinyradius.client.handler.ClientPacketCodec;
import org.tinyradius.client.handler.PromiseAdapter;

public class ProxyChannelInitializer extends ChannelInitializer<DatagramChannel> {
    private final ClientPacketCodec clientPacketCodec;

    public ProxyChannelInitializer(ClientPacketCodec clientPacketCodec) {
        super();
        this.clientPacketCodec = clientPacketCodec;
    }

    @Override
    protected void initChannel(DatagramChannel ch) {
        ch.pipeline().addLast(new PromiseAdapter(), clientPacketCodec);
    }
}
