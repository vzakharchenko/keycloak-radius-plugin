package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals.AttributeConditional;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals.RoleServiceTypeAttributeConditional;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RoleModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
    protected List<String> getAttributes(RoleModel roleModel,
                                         String attributeName) {
        List<String> attributes = new ArrayList<>();
        roleModel.getAttributes().entrySet().stream().filter(entry ->
                attributeName.equalsIgnoreCase(entry.getKey())).forEach(entry -> {
            if (entry.getValue() != null) {
                attributes.addAll(entry.getValue());
            }
        });
        return attributes;
    }

    @Override
    protected List<AttributeConditional<RoleModel>> getAttributeConditional() {
        return Collections.singletonList(
                new RoleServiceTypeAttributeConditional(session));
    }
}
