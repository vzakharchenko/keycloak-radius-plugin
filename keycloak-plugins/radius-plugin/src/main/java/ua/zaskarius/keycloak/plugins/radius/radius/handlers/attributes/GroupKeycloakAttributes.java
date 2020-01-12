package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals.AttributeConditional;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals.GroupServiceTypeAttributeConditional;

import java.util.*;

public class GroupKeycloakAttributes extends AbstractKeycloakAttributes<GroupModel> {

    public GroupKeycloakAttributes(KeycloakSession session) {
        super(session);
    }

    @Override
    protected KeycloakAttributesType getType() {
        return KeycloakAttributesType.GROUP;
    }

    @Override
    protected Set<GroupModel> getKeycloakTypes() {
        return radiusUserInfo.getUserModel().getGroups();
    }

    @Override
    protected Map<String, Set<String>> getAttributes(GroupModel groupModel) {
        Map<String, Set<String>> attributes = new HashMap<>();
        AttributeWalkerUtils.groupWalker(groupModel, attributes);
        return attributes;
    }

    @Override
    protected List<AttributeConditional<GroupModel>> getAttributeConditional() {
        return Collections.singletonList(
                new GroupServiceTypeAttributeConditional(session));
    }
}
