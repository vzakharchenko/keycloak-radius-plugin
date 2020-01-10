package ua.zaskarius.keycloak.plugins.radius.radius.server;

import com.google.common.annotations.VisibleForTesting;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.Future;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProvider;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.AuthHandler;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.IKeycloakSecretProvider;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.KeycloakSecretProvider;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.dictionary.DefaultDictionary;
import org.tinyradius.dictionary.DictionaryParser;
import org.tinyradius.dictionary.WritableDictionary;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.PacketEncoder;
import org.tinyradius.server.HandlerAdapter;
import org.tinyradius.server.RadiusServer;
import org.tinyradius.server.SecretProvider;
import org.tinyradius.server.handler.AcctHandler;
import org.tinyradius.server.handler.DeduplicatorHandler;
import org.tinyradius.server.handler.RequestHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class KeycloakRadiusServer
        implements IRadiusServerProvider {

    private static final Logger LOGGER = Logger
            .getLogger(KeycloakRadiusServer.class);
    public static final int TTL_MS = 10000;
    public static final int AUTH_PORT = 1812;
    public static final int ACCOUNT_PORT = 1813;
    public static final String MIKROTIK = "mikrotik";
    public static final String MS = "MS";

    private RadiusServer server;

    private WritableDictionary getDictionary() {
        WritableDictionary instance = DefaultDictionary.INSTANCE;
        try {
            DictionaryParser dictionaryParser = DictionaryParser.newClasspathParser();
            dictionaryParser
                    .parseDictionary(instance,
                            MIKROTIK);
            dictionaryParser
                    .parseDictionary(instance,
                            MS);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return instance;
    }

    public KeycloakRadiusServer(KeycloakSession session) {
        IKeycloakSecretProvider secretProvider = new KeycloakSecretProvider(session);

        final PacketEncoder packetEncoder = new PacketEncoder(getDictionary());
        final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
        HashedWheelTimer timer = new HashedWheelTimer(1, TimeUnit.SECONDS);
        final RequestHandler<AccessRequest,
                IKeycloakSecretProvider> authHandler = new DeduplicatorHandler(
                new AuthHandler(session),
                timer,
                TTL_MS);
        RequestHandler<AccountingRequest,
                SecretProvider> acctHandler = new DeduplicatorHandler<>(
                new AcctHandler(), timer, TTL_MS);
        server = new RadiusServer(eventLoopGroup,
                timer,
                new ReflectiveChannelFactory<>(NioDatagramChannel.class),
                new HandlerAdapter<>(packetEncoder,
                        authHandler, timer, secretProvider, AccessRequest.class),
                new HandlerAdapter<>(packetEncoder,
                        acctHandler, timer, secretProvider, AccountingRequest.class),
                new InetSocketAddress(AUTH_PORT), new InetSocketAddress(ACCOUNT_PORT));
        final Future<Void> future = server.start();
        future.addListener(future1 -> {
            if (future1.isSuccess()) {
                LOGGER.info("Server started");
            } else {
                LOGGER.info("Failed to start server: " + future1.cause());
                server.stop().syncUninterruptibly();
                eventLoopGroup.shutdownGracefully();
            }
        });
    }

    @Override
    public void close() {
    }

    @VisibleForTesting
    RadiusServer getServer() {
        return server;
    }
}
