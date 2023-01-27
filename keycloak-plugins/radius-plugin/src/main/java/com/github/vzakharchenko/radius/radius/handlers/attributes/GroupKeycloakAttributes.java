package com.github.vzakharchenko.radius.radius.handlers.attributes;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.GroupModel;
import org.tinyradius.packet.AccessRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class  GroupKeycloakAttributes extends AbstractKeycloakAttributes<GroupModel> {

    public GroupKeycloakAttributes(KeycloakSession session,
                                   AccessRequest accessRequest) {
        super(session, accessRequest);
    }

    @Override
    protected KeycloakAttributesType getType() {
        return KeycloakAttributesType.GROUP;
    }

    @Override
    protected Set<GroupModel> getKeycloakTypes() {
        return radiusUserInfoGetter.getRadiusUserInfo().getUserModel().getGroupsStream()
                .collect(Collectors.toSet());
    }

    @Override
    protected Map<String, Set<String>> getAttributes(GroupModel groupModel) {
        Map<String, Set<String>> attributes = new HashMap<>();
        AttributeWalkerUtils.groupWalker(groupModel, attributes);
        return filter(attributes);
    }
}
