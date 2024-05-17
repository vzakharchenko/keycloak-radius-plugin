package com.github.vzakharchenko.radsec.server;

import com.github.vzakharchenko.radius.configuration.IRadiusConfiguration;
import com.github.vzakharchenko.radius.configuration.RadiusConfigHelper;
import com.github.vzakharchenko.radius.models.RadSecSettings;
import com.github.vzakharchenko.radius.providers.AbstractRadiusServerProvider;
import com.github.vzakharchenko.radius.radius.handlers.KeycloakSecretProvider;
import com.github.vzakharchenko.radius.radius.server.KeycloakRadiusServer;
import com.github.vzakharchenko.radsec.codec.RadSecCodec;
import com.github.vzakharchenko.radsec.providers.IRadiusRadSecHandlerProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.packet.PacketEncoder;
import org.tinyradius.server.SecretProvider;

import java.io.File;

public final class RadSecServerProvider
        extends AbstractRadiusServerProvider implements IRadSecServerProvider {

    private static final Logger LOGGER = Logger.getLogger(KeycloakRadiusServer.class);

    private ChannelFuture channelFuture;

    @Override
    public ChannelHandler radsecChannel(KeycloakSession session) {
        IRadiusRadSecHandlerProvider provider = session
                .getProvider(IRadiusRadSecHandlerProvider.class);
        return provider.getChannelHandler(session);
    }

    @Override
    public ChannelHandler createHandler(Channel ch) {
        RadSecSettings radSecSettings = RadiusConfigHelper.getConfig()
                .getRadiusSettings()
                .getRadSecSettings();
        File privateKey = new File(radSecSettings.getPrivKey());
        File certificate = new File(radSecSettings.getCert());
        if (!privateKey.exists() || !certificate.exists()) {
            throw new IllegalStateException("wrong RadSec configuration. " +
                    "Private ( " + privateKey
                    .getAbsolutePath() + ") or Certificate (" + certificate
                    .getAbsolutePath() + ") does not exists");
        }
        try {
            SslContext sslCtx = SslContextBuilder.forServer(certificate, privateKey)
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build(); //todo
            return sslCtx.newHandler(ch.alloc());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    public RadSecServerProvider(KeycloakSession session) {
        super();
        IRadiusConfiguration radiusConfiguration = RadiusConfigHelper.getConfig();
        RadSecSettings radSecSettings = radiusConfiguration.getRadiusSettings().getRadSecSettings();
        if (radSecSettings.isUseRadSec()) {
            SecretProvider secretProvider = new KeycloakSecretProvider();
            final PacketEncoder packetEncoder = createPacketEncoder(session);
            EventLoopGroup bossGroup = new NioEventLoopGroup(radSecSettings.getnThreads());
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            channelFuture = serverBootstrap
                    .channel(NioServerSocketChannel.class).group(bossGroup).clone()
                    .childHandler(new RadSecChannelInitializer(this,
                            new LoggingHandler(LogLevel.TRACE),
                            new RadSecCodec(packetEncoder, secretProvider), session))
                    .bind(2083);
            channelFuture.addListener(f -> {
                if (f.isSuccess()) {
                    LOGGER.info("Server started");
                } else {
                    LOGGER.info("Could not start server");
                    stop();
                }
            });
        }
    }

    public void stop() {
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
    }
}
