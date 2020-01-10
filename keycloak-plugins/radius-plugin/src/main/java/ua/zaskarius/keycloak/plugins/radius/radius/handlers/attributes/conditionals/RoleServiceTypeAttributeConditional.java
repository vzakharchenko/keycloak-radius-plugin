package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RoleModel;

import java.util.List;

public class RoleServiceTypeAttributeConditional
        extends AbstractServiceTypeAttributeConditional<RoleModel> {


    public RoleServiceTypeAttributeConditional(KeycloakSession session) {
        super(session);
    }

    @Override
    protected List<String> getServiceTypes(RoleModel roleModel) {
        return roleModel.getAttribute(SERVICE_TYPE);
    }

    @Override
    protected List<String> getProtocolTypes(RoleModel roleModel) {
        return roleModel.getAttribute(PROTOCOL_TYPE);
    }
}
