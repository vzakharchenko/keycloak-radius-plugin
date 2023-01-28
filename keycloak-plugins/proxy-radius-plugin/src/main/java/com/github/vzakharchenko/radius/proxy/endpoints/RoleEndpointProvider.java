package com.github.vzakharchenko.radius.proxy.endpoints;

import com.github.vzakharchenko.radius.radius.handlers.attributes.AttributeWalkerUtils;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleEndpointProvider extends AbstractProxyEndpointProvider<RoleModel> {

    public static final String RADIUS_ROLE_PROXY_ENDPOINT = "radius-role-proxy-endpoint";

    @Override
    protected void walker(RoleModel roleModel, Map<String, Set<String>> attributes) {
        AttributeWalkerUtils.roleWalker(roleModel, attributes);
    }

    @Override
    protected Collection<RoleModel> getTypes(UserModel userModel) {
        return userModel.getRoleMappingsStream().collect(Collectors.toSet());
    }

    @Override
    public String getId() {
        return RADIUS_ROLE_PROXY_ENDPOINT;
    }
}
