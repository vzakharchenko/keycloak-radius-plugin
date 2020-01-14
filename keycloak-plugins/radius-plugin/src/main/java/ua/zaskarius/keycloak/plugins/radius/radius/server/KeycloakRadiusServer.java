package ua.zaskarius.keycloak.plugins.radius.radius.server;

import com.google.common.annotations.VisibleForTesting;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RequiredActionProviderModel;
import org.tinyradius.packet.PacketEncoder;
import org.tinyradius.server.RadiusServer;
import org.tinyradius.server.handler.ServerPacketCodec;
import ua.zaskarius.keycloak.plugins.radius.configuration.RadiusConfigHelper;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAccountHandlerProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAuthHandlerProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProvider;
import ua.zaskarius.keycloak.plugins.radius.radius.dictionary.DictionaryLoader;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.IKeycloakSecretProvider;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.KeycloakSecretProvider;

import java.net.InetSocketAddress;

import static ua.zaskarius.keycloak.plugins.radius.password.UpdateRadiusPassword.RADIUS_UPDATE_PASSWORD;
import static ua.zaskarius.keycloak.plugins.radius.password.UpdateRadiusPassword.UPDATE_RADIUS_PASSWORD_ID;

public class KeycloakRadiusServer
        implements IRadiusServerProvider {

    private static final Logger LOGGER = Logger.getLogger(KeycloakRadiusServer.class);
    public static final String MIKROTIK = "mikrotik";
    public static final String MS = "MS";
    public static final int N_THREADS = 10;

    private final RadiusServer server;


    private EventLoopGroup createEventLoopGroup() {
        return new NioEventLoopGroup(N_THREADS);
    }

    private PacketEncoder createPacketEncoder(KeycloakSession session) {
        return new PacketEncoder(
                DictionaryLoader
                        .getInstance()
                        .loadDictionary(session));
    }

    private ChannelHandler accountChannel(KeycloakSession session) {
        IRadiusAccountHandlerProvider provider = session
                .getProvider(IRadiusAccountHandlerProvider.class);
        return provider.getChannelHandler(session);
    }

    private ChannelHandler authChannel(KeycloakSession session) {
        IRadiusAuthHandlerProvider provider = session
                .getProvider(IRadiusAuthHandlerProvider.class);
        return provider.getChannelHandler(session);
    }

    private RadiusServer createRadiusServer(KeycloakSession session,
                                            Bootstrap bootstrap,
                                            RadiusServerSettings radiusSettings) {
        IKeycloakSecretProvider secretProvider = new KeycloakSecretProvider();
        final PacketEncoder packetEncoder = createPacketEncoder(session);
        final ServerPacketCodec serverPacketCodec = new ServerPacketCodec(packetEncoder,
                secretProvider);
        return new RadiusServer(bootstrap,
                new ChannelInitializer<DatagramChannel>() {
                    @Override
                    protected void initChannel(DatagramChannel ch) {
                        ch.pipeline().addLast(serverPacketCodec, authChannel(session));
                    }
                },
                new ChannelInitializer<DatagramChannel>() {
                    @Override
                    protected void initChannel(DatagramChannel ch) {
                        ch.pipeline().addLast(serverPacketCodec, accountChannel(session));
                    }
                },
                new InetSocketAddress(radiusSettings.getAuthPort()), new InetSocketAddress(
                radiusSettings.getAccountPort()));
    }

    public KeycloakRadiusServer(KeycloakSession session) {
        RadiusServerSettings radiusSettings = RadiusConfigHelper
                .getConfig().getRadiusSettings();
        final EventLoopGroup eventLoopGroup = createEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap()
                .channel(NioDatagramChannel.class).group(eventLoopGroup);
        server = createRadiusServer(session, bootstrap, radiusSettings);
        if (radiusSettings.isUseRadius()) {
            server.isReady().addListener(future1 -> {
                if (future1.isSuccess()) {
                    LOGGER.info("Server started");
                } else {
                    LOGGER.info("Failed to start server: " + future1.cause());
                    server.close();
                    eventLoopGroup.shutdownGracefully();
                }
            });
        }
    }

    @Override
    public void close() {
    }

    @Override
    public String fieldName() {
        return "preferred_username";
    }

    @Override
    public String fieldPassword() {
        return "s";
    }

    @Override
    public boolean init(RealmModel realmModel) {
        boolean changed = false;
        if (realmModel
                .getRequiredActionProviderByAlias(
                        RADIUS_UPDATE_PASSWORD) == null) {
            RequiredActionProviderModel updatePassword = new RequiredActionProviderModel();
            updatePassword.setEnabled(true);
            updatePassword.setAlias(RADIUS_UPDATE_PASSWORD);
            updatePassword.setName("Update Radius Password");
            updatePassword.setProviderId(UPDATE_RADIUS_PASSWORD_ID);
            updatePassword.setDefaultAction(false);
            updatePassword.setPriority(30);
            realmModel.addRequiredActionProvider(updatePassword);
            changed = true;
        }
        return changed;
    }

    @VisibleForTesting
    RadiusServer getServer() {
        return server;
    }
}
