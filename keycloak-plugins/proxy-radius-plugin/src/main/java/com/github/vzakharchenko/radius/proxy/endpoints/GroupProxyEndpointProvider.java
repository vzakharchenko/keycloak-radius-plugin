package com.github.vzakharchenko.radius.proxy.endpoints;

import com.github.vzakharchenko.radius.radius.handlers.attributes.AttributeWalkerUtils;
import org.keycloak.models.GroupModel;
import org.keycloak.models.UserModel;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class GroupProxyEndpointProvider extends AbstractProxyEndpointProvider<GroupModel> {

    public static final String RADIUS_GROUP_PROXY_ENDPOINT = "radius-group-proxy-endpoint";

    @Override
    protected void walker(GroupModel groupModel, Map<String, Set<String>> attributes) {
        AttributeWalkerUtils.groupWalker(groupModel, attributes);
    }

    @Override
    protected Collection<GroupModel> getTypes(UserModel userModel) {
        return userModel.getGroups();
    }

    @Override
    public String getId() {
        return RADIUS_GROUP_PROXY_ENDPOINT;
    }
}
