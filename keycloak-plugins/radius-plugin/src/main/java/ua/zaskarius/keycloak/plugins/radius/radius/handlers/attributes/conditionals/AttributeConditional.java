package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals;

import ua.zaskarius.keycloak.plugins.radius.models.RadiusAttributeHolder;
import org.tinyradius.packet.AccessRequest;

public interface AttributeConditional<T> {
    boolean useAttributes(RadiusAttributeHolder<T> radiusAttributeHolder,
                          AccessRequest accessRequest);
}
