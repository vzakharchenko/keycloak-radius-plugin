package com.github.vzakharchenko.radius.proxy;

import com.github.vzakharchenko.radius.providers.IRadiusProxyProvider;
import com.github.vzakharchenko.radius.providers.IRadiusProxyProviderFactory;
import com.github.vzakharchenko.radius.proxy.proxy.IComponentProxy;
import com.github.vzakharchenko.radius.proxy.proxy.RadiusComponentProxy;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.server.RequestCtx;

public class RadiusProxy implements IRadiusProxyProvider, IRadiusProxyProviderFactory {

    public static final String RADIUS_PROXY = "radius-proxy";

    private IComponentProxy componentProxy = new RadiusComponentProxy();

    @Override
    public RadiusPacket proxy(KeycloakSession session, RequestCtx msg, RadiusPacket answer) {
        RadiusPacket request = msg.getRequest();
        if (request.getClass().equals(AccessRequest.class)) {
            return componentProxy.proxy(session,
                    msg.getRequest(), answer);
        } else if (request.getClass().equals(AccountingRequest.class)) {
            return componentProxy.proxy(session,
                    msg.getRequest(), answer);
        } else {
            throw new IllegalStateException(request + " (" + request
                    .getClass() + ") does not have Handler");
        }
    }

    @Override
    public IRadiusProxyProvider create(KeycloakSession session) {
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
        return RADIUS_PROXY;
    }

    // use for testing only
    void setComponentProxy(IComponentProxy componentProxy) {
        this.componentProxy = componentProxy;
    }
}
