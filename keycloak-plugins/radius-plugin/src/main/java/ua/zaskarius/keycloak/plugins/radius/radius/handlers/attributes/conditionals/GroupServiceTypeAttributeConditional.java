package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals;

import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;

import java.util.List;

public class GroupServiceTypeAttributeConditional
        extends AbstractServiceTypeAttributeConditional<GroupModel> {


    public GroupServiceTypeAttributeConditional(KeycloakSession session) {
        super(session);
    }

    @Override
    protected List<String> getServiceTypes(GroupModel groupModel) {
        return groupModel.getAttribute(SERVICE_TYPE);
    }

    @Override
    protected List<String> getProtocolTypes(GroupModel groupModel) {
        return groupModel.getAttribute(PROTOCOL_TYPE);
    }
}
