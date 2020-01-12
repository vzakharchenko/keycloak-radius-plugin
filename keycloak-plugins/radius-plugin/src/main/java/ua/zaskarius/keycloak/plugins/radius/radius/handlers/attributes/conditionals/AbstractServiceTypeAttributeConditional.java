package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals;

import ua.zaskarius.keycloak.plugins.radius.models.RadiusAttributeHolder;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusUserInfo;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.session.KeycloakSessionUtils;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.packet.AccessRequest;

import java.util.List;

public abstract class AbstractServiceTypeAttributeConditional<T>
        implements AttributeConditional<T> {

    public static final String SERVICE_TYPE = "Service-Type";
    public static final String PROTOCOL_TYPE = "Protocol-Type";

    protected final RadiusUserInfo radiusUserInfo;

    public AbstractServiceTypeAttributeConditional(KeycloakSession session) {
        this.radiusUserInfo = KeycloakSessionUtils
                .getRadiusUserInfo(session);
    }

    protected abstract List<String> getServiceTypes(T t);

    protected abstract List<String> getProtocolTypes(T t);

    private String getServiceType(AccessRequest accessRequest) {
        RadiusAttribute attribute = accessRequest.getAttribute("Service-Type");
        if (attribute != null) {
            return attribute.getValueString();
        } else {
            return null;
        }
    }


    @Override
    public boolean useAttributes(RadiusAttributeHolder<T> radiusAttributeHolder,
                                 AccessRequest accessRequest) {
        T object = radiusAttributeHolder.getObject();
        List<String> serviceTypes = getServiceTypes(object);
        String serviceType = getServiceType(accessRequest);
        if (serviceType != null) {
            if (serviceTypes != null && serviceTypes.contains(serviceType)) {
                List<String> protocolTypes = getProtocolTypes(object);
                if (protocolTypes != null && !protocolTypes.isEmpty()) {

                    return protocolTypes.contains(radiusUserInfo
                            .getProtocol().getType().name());
                }
                return true;
            }
        }
        return true;
    }
}
