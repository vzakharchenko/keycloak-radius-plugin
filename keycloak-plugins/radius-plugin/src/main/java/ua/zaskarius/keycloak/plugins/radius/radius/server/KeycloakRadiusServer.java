package ua.zaskarius.keycloak.plugins.radius.radius.server;

import com.google.common.annotations.VisibleForTesting;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.Future;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RequiredActionProviderModel;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.PacketEncoder;
import org.tinyradius.server.HandlerAdapter;
import org.tinyradius.server.RadiusServer;
import org.tinyradius.server.SecretProvider;
import org.tinyradius.server.handler.AcctHandler;
import org.tinyradius.server.handler.DeduplicatorHandler;
import org.tinyradius.server.handler.RequestHandler;
import ua.zaskarius.keycloak.plugins.radius.configuration.RadiusConfigHelper;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProvider;
import ua.zaskarius.keycloak.plugins.radius.radius.dictionary.DictionaryLoader;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.AuthHandler;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.IKeycloakSecretProvider;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.KeycloakSecretProvider;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static ua.zaskarius.keycloak.plugins.radius.password.UpdateRadiusPassword.RADIUS_UPDATE_PASSWORD;
import static ua.zaskarius.keycloak.plugins.radius.password.UpdateRadiusPassword.UPDATE_RADIUS_PASSWORD_ID;

public class KeycloakRadiusServer
        implements IRadiusServerProvider {

    private static final Logger LOGGER = Logger
            .getLogger(KeycloakRadiusServer.class);
    public static final int TTL_MS = 10000;
    public static final String MIKROTIK = "mikrotik";
    public static final String MS = "MS";
    public static final int N_THREADS = 4;
    private HashedWheelTimer timer = new HashedWheelTimer(1, TimeUnit.SECONDS);

    private RadiusServer server;


    private EventLoopGroup createEventLoopGroup() {
        return new NioEventLoopGroup(N_THREADS);
    }

    private PacketEncoder createPacketEncoder(KeycloakSession session) {
        return new PacketEncoder(
                DictionaryLoader
                        .getInstance()
                        .loadDictionary(session));
    }

    private RequestHandler<AccessRequest,
            IKeycloakSecretProvider> createAuthHandler(KeycloakSession session) {
        return new DeduplicatorHandler(
                new AuthHandler(session),
                timer,
                TTL_MS);
    }

    private RequestHandler<AccountingRequest,
            SecretProvider> createAccountHandler() {
        return new DeduplicatorHandler<>(
                new AcctHandler(), timer, TTL_MS);
    }

    private RadiusServer createRadiusServer(KeycloakSession session,
                                            EventLoopGroup eventLoopGroup,
                                            RadiusServerSettings radiusSettings) {
        IKeycloakSecretProvider secretProvider = new KeycloakSecretProvider(session);
        final PacketEncoder packetEncoder = createPacketEncoder(session);
        final RequestHandler<AccessRequest,
                IKeycloakSecretProvider> authHandler = createAuthHandler(session);
        final RequestHandler<AccountingRequest,
                SecretProvider> acctHandler = createAccountHandler();
        return new RadiusServer(eventLoopGroup,
                timer,
                new ReflectiveChannelFactory<>(NioDatagramChannel.class),
                new HandlerAdapter<>(packetEncoder,
                        authHandler, timer, secretProvider, AccessRequest.class),
                new HandlerAdapter<>(packetEncoder,
                        acctHandler, timer, secretProvider, AccountingRequest.class),
                new InetSocketAddress(radiusSettings.getAuthPort()), new InetSocketAddress(
                radiusSettings.getAccountPort()));
    }

    public KeycloakRadiusServer(KeycloakSession session) {
        RadiusServerSettings radiusSettings = RadiusConfigHelper
                .getConfig().getRadiusSettings(session);
        final EventLoopGroup eventLoopGroup = createEventLoopGroup();
        server = createRadiusServer(session, eventLoopGroup, radiusSettings);
        if (radiusSettings.isUseRadius()) {
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
