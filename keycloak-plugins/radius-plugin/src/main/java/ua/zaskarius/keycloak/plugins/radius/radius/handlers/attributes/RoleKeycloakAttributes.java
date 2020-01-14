package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RoleModel;
import org.tinyradius.packet.AccessRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RoleKeycloakAttributes extends AbstractKeycloakAttributes<RoleModel> {

    public RoleKeycloakAttributes(KeycloakSession session, AccessRequest accessRequest) {
        super(session, accessRequest);
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
        return filter(attributes);
    }

}
