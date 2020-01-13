package ua.zaskarius.keycloak.plugins.radius.radius.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.packet.RadiusPackets;
import org.tinyradius.server.RequestCtx;
import org.tinyradius.server.handler.RequestHandler;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAccountHandlerProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAccountHandlerProviderFactory;

import static org.tinyradius.packet.PacketType.ACCOUNTING_RESPONSE;

public class AccountingHandler
        extends RequestHandler
        implements IRadiusAccountHandlerProviderFactory, IRadiusAccountHandlerProvider {


    public static final String DEFAULT_ACCOUNT_RADIUS_PROVIDER = "default-account-radius-provider";

    @Override
    protected Class<AccountingRequest> acceptedPacketType() {
        return AccountingRequest.class;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestCtx msg) {
        final RadiusPacket request = msg.getRequest();

        RadiusPacket answer = RadiusPackets.create(request.getDictionary(),
                ACCOUNTING_RESPONSE, request.getIdentifier());
        request.getAttributes(33).forEach(answer::addAttribute);

        ctx.writeAndFlush(msg.withResponse(answer));
    }

    @Override
    public IRadiusAccountHandlerProvider create(KeycloakSession session) {
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
        return DEFAULT_ACCOUNT_RADIUS_PROVIDER;
    }

    @Override
    public ChannelHandler getChannelHandler(KeycloakSession session) {
        return (ChannelHandler) create(session);
    }
}
