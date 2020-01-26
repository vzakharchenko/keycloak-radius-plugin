package com.github.vzakharchenko.radius.providers;

import org.keycloak.provider.Provider;
import org.tinyradius.packet.AccessRequest;

public interface IRadiusServiceProvider extends Provider {
    /**
     * Keycloak attribute name (Group, Role)
     * used for Role and Group validation
     * @return Keycloak attribute name
     */
    String attributeName();

    /**
     * name of service
     * @return name of service
     */
    String serviceName();

    /**
     * check if the accessRequest is a service
     * @param accessRequest
     * @return true if service otherwise false
     */
    boolean checkService(AccessRequest accessRequest);
}
