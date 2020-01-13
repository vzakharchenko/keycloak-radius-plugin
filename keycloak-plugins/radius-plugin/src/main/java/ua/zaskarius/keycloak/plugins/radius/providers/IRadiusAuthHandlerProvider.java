package ua.zaskarius.keycloak.plugins.radius.providers;

import io.netty.channel.ChannelHandler;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.Provider;

public interface IRadiusAuthHandlerProvider extends Provider {
    ChannelHandler getChannelHandler(KeycloakSession session);
}
