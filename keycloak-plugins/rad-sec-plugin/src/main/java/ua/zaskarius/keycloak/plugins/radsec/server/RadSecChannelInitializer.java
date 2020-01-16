package ua.zaskarius.keycloak.plugins.radsec.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.keycloak.models.KeycloakSession;

public class RadSecChannelInitializer extends ChannelInitializer<NioSocketChannel> {
    private final IRadSecServerProvider sslProvider;
    private final ChannelHandler logger;
    private final ChannelHandler codec;
    private final ChannelHandler handler;

    public RadSecChannelInitializer(IRadSecServerProvider sslProvider,
                                    ChannelHandler logger,
                                    ChannelHandler codec,
                                    KeycloakSession session) {
        super();
        this.sslProvider = sslProvider;
        this.logger = logger;
        this.codec = codec;
        this.handler = sslProvider.radsecChannel(session);
    }

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline().addFirst(sslProvider.createHandler(ch));
        ch.pipeline().addLast(logger);
        ch.pipeline().addLast(codec);
        ch.pipeline().addLast(handler);
    }
}
