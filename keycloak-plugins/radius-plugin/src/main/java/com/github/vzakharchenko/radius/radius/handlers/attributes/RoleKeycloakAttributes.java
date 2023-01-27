package com.github.vzakharchenko.radius.radius.handlers.attributes;

import org.keycloak.models.KeycloakSession;
import org.tinyradius.packet.AccessRequest;
import org.keycloak.models.RoleModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        return radiusUserInfoGetter.getRadiusUserInfo().getUserModel().getRoleMappingsStream()
            .collect(Collectors.toSet());
    }

    @Override
    protected Map<String, Set<String>> getAttributes(RoleModel roleModel) {
        Map<String, Set<String>> attributes = new HashMap<>();
        AttributeWalkerUtils.roleWalker(roleModel, attributes);
        return filter(attributes);
    }

}
