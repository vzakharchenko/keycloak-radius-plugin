package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.packet.AccessRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GroupKeycloakAttributes extends AbstractKeycloakAttributes<GroupModel> {

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
        return radiusUserInfo.getUserModel().getGroups();
    }

    @Override
    protected Map<String, Set<String>> getAttributes(GroupModel groupModel) {
        Map<String, Set<String>> attributes = new HashMap<>();
        AttributeWalkerUtils.groupWalker(groupModel, attributes);
        return filter(attributes);
    }
}
