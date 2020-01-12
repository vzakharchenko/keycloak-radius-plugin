package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RoleModel;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals.AttributeConditional;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals.RoleServiceTypeAttributeConditional;

import java.util.*;

public class RoleKeycloakAttributes extends AbstractKeycloakAttributes<RoleModel> {

    public RoleKeycloakAttributes(KeycloakSession session) {
        super(session);
    }

    @Override
    protected KeycloakAttributesType getType() {
        return KeycloakAttributesType.ROLE;
    }

    @Override
    protected Set<RoleModel> getKeycloakTypes() {
        return radiusUserInfo.getUserModel().getRoleMappings();
    }

    @Override
    protected Map<String, Set<String>> getAttributes(RoleModel roleModel) {
        Map<String, Set<String>> attributes = new HashMap<>();
        AttributeWalkerUtils.roleWalker(roleModel, attributes);
        return attributes;
    }

    @Override
    protected List<AttributeConditional<RoleModel>> getAttributeConditional() {
        return Collections.singletonList(
                new RoleServiceTypeAttributeConditional(session));
    }
}
