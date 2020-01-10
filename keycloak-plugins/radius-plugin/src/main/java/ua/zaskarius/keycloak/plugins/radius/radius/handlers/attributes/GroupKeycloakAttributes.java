package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals.AttributeConditional;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals.GroupServiceTypeAttributeConditional;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
    protected List<String> getAttributes(GroupModel groupModel,
                                         String attributeName) {
        List<String> attributes = new ArrayList<>();
        groupModel.getAttributes().entrySet().stream().filter(entry ->
                attributeName.equalsIgnoreCase(entry.getKey())).forEach(entry -> {
            if (entry.getValue() != null) {
                attributes.addAll(entry.getValue());
            }
        });
        return attributes;
    }

    @Override
    protected List<AttributeConditional<GroupModel>> getAttributeConditional() {
        return Collections.singletonList(
                new GroupServiceTypeAttributeConditional(session));
    }
}
