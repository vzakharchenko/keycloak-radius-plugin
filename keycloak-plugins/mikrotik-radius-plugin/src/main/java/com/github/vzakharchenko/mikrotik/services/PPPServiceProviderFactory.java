package com.github.vzakharchenko.mikrotik.services;

import com.github.vzakharchenko.mikrotik.MikrotikConstantUtils;
import com.github.vzakharchenko.radius.providers.IRadiusServiceProvider;
import com.github.vzakharchenko.radius.providers.IRadiusServiceProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.packet.AccessRequest;

public class PPPServiceProviderFactory
        implements IRadiusServiceProviderFactory, IRadiusServiceProvider {

    public static final String MIKROTIK_PPP_SERVICE = "mikrotik-ppp-service";

    @Override
    public IRadiusServiceProvider create(KeycloakSession session) {
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
        return MIKROTIK_PPP_SERVICE;
    }

    @Override
    public String attributeName() {
        return MikrotikConstantUtils.MIKROTIK_SERVICE_ATTRIBUTE;
    }

    @Override
    public String serviceName() {
        return "ppp";
    }

    @Override
    public boolean checkService(AccessRequest accessRequest) {
        RadiusAttribute serviceTypeAttribute = accessRequest.getAttribute("Framed-Protocol");
        return serviceTypeAttribute != null && "PPP"
                .equalsIgnoreCase(serviceTypeAttribute.getValueString());
    }
}
