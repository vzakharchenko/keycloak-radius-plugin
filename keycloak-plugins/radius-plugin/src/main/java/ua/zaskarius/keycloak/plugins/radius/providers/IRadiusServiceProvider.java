package ua.zaskarius.keycloak.plugins.radius.providers;

import org.keycloak.provider.Provider;
import org.tinyradius.packet.AccessRequest;

public interface IRadiusServiceProvider extends Provider {
    String attrbuteName();

    String serviceName();

    boolean checkService(AccessRequest accessRequest);
}
