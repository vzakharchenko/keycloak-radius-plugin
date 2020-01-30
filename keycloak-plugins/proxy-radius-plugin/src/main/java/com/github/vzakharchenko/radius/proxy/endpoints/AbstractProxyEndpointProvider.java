package com.github.vzakharchenko.radius.proxy.endpoints;

import com.github.vzakharchenko.radius.proxy.models.ProxyModel;
import com.github.vzakharchenko.radius.proxy.providers.IRadiusProxyEndpointProvider;
import com.github.vzakharchenko.radius.proxy.providers.IRadiusProxyEndpointProviderFactory;
import com.github.vzakharchenko.radius.radius.handlers.session.KeycloakSessionUtils;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfo;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.UserModel;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusEndpoint;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractProxyEndpointProvider<T>
        implements IRadiusProxyEndpointProvider, IRadiusProxyEndpointProviderFactory {

    protected abstract void walker(T t, Map<String, Set<String>> attributes);

    protected Map<String, String> getAttributes(T t) {
        Map<String, Set<String>> attributes = new HashMap<>();
        walker(t, attributes);
        return attributes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> entry.getValue().isEmpty() ? "" : entry.getValue().iterator().next()));
    }

    protected abstract Collection<T> getTypes(UserModel userModel);

    protected ProxyModel getProxyModel(T t,
                                       Class<? extends RadiusPacket> packetType) {
        String address = getAddress(t, packetType);
        int port = getPort(t, packetType);
        String secret = getSecret(t, packetType);
        return new ProxyModel(address, port, secret);
    }

    protected ProxyModel getProxyModel(Collection<T> types,
                                       Class<? extends RadiusPacket> packetType) {
        for (T t : types) {
            ProxyModel proxyModel = getProxyModel(t, packetType);
            if (proxyModel.isValid()) {
                return proxyModel;
            }
        }
        return null;
    }

    @Override
    public RadiusEndpoint getRadiusEndpoint(KeycloakSession session,
                                            Class<? extends RadiusPacket> packetType) {
        IRadiusUserInfo radiusSessionInfo = KeycloakSessionUtils.getRadiusSessionInfo(session);
        ProxyModel proxyModel = null;
        if (radiusSessionInfo != null) {
            Collection<T> types = getTypes(radiusSessionInfo.getUserModel());
            proxyModel = getProxyModel(types, packetType);
        }
        if (proxyModel != null) {
            return new RadiusEndpoint(new InetSocketAddress(proxyModel.getAddress(),
                    proxyModel.getPort()), proxyModel.getSecret());
        } else {
            return null;
        }
    }


    protected String getSecret(T t, Class<? extends RadiusPacket> packetType) {
        return getSecret(t, packetType.getSimpleName() + "Secret");
    }

    private int getPort(T t, Class<? extends RadiusPacket> packetType) {
        return getPort(t, packetType.getSimpleName() + "Port");
    }

    private String getAddress(T t, Class<? extends RadiusPacket> packetType) {
        return getAddress(t, packetType.getSimpleName() + "Address");
    }

    protected String getAttribute(T t, String attributeName) {
        Map<String, String> attributes = getAttributes(t);
        return attributes.get(attributeName);
    }

    protected String getSecret(T t, String attributeName) {
        return getAttribute(t, attributeName);
    }


    protected int getPort(T t, String attributeName) {
        String s = getAttribute(t, attributeName);
        return s == null ? 0 : Integer.parseInt(s);
    }


    protected String getAddress(T t, String attributeName) {
        return getAttribute(t, attributeName);
    }

    @Override
    public IRadiusProxyEndpointProvider create(KeycloakSession session) {
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

}
