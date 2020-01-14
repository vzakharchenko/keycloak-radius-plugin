package ua.in.zaskarius.keycloak.mikrotik.services;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.packet.AccessRequest;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServiceProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServiceProviderFactory;

import static org.tinyradius.packet.AccessRequest.AUTH_CHAP;
import static org.tinyradius.packet.AccessRequest.AUTH_PAP;
import static ua.in.zaskarius.keycloak.mikrotik.MikrotikConstantUtils.MIKROTIK_SERVICE_ATTRIBUTE;

public class HotSpotServiceProviderFactory
        implements IRadiusServiceProviderFactory, IRadiusServiceProvider {

    public static final String MIKROTIK_HOTSPOT_SERVICE = "mikrotik-hotspot-service";

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
        return MIKROTIK_HOTSPOT_SERVICE;
    }

    @Override
    public String attrbuteName() {
        return MIKROTIK_SERVICE_ATTRIBUTE;
    }

    @Override
    public String serviceName() {
        return "hotspot";
    }

    @Override
    public boolean checkService(AccessRequest accessRequest) {
        RadiusAttribute serviceTypeAttribute = accessRequest.getAttribute("Service-Type");
        return serviceTypeAttribute != null && "Login-User"
                .equalsIgnoreCase(serviceTypeAttribute.getValueString())
                && (AUTH_PAP
                .equalsIgnoreCase(accessRequest.getAuthProtocol()) ||
                AUTH_CHAP.equalsIgnoreCase(accessRequest.getAuthProtocol()));
    }
}
