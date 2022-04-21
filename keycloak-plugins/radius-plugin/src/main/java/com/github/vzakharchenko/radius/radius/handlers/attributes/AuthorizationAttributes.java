package com.github.vzakharchenko.radius.radius.handlers.attributes;

import com.github.vzakharchenko.radius.radius.handlers.attributes.permissions.RadiusEvaluationDecisionCollector;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfo;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.Decision;
import org.keycloak.authorization.common.DefaultEvaluationContext;
import org.keycloak.authorization.common.UserModelIdentity;
import org.keycloak.authorization.model.Resource;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.permission.ResourcePermission;
import org.keycloak.authorization.permission.evaluator.PermissionEvaluator;
import org.keycloak.authorization.policy.evaluation.Result;
import org.keycloak.authorization.store.StoreFactory;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.tinyradius.packet.AccessRequest;

import java.util.*;
import java.util.stream.Collectors;

public class AuthorizationAttributes extends AbstractKeycloakAttributes<Resource> {

    public AuthorizationAttributes(KeycloakSession session, AccessRequest accessRequest) {
        super(session, accessRequest);
    }

    @Override
    protected KeycloakAttributesType getType() {
        return KeycloakAttributesType.AUTHORIZATION;
    }

    protected Set<Resource> getKeycloakTypes(AuthorizationProvider authorizationProvider,
                                             ResourceServer resourceServer,
                                             UserModel userModel, RealmModel realmModel) {
        StoreFactory storeFactory = authorizationProvider.getStoreFactory();
        List<Resource> resources = storeFactory
                .getResourceStore().findByResourceServer(resourceServer);
        PermissionEvaluator permissionEvaluator = authorizationProvider.evaluators()
                .from(resources.stream().map(resource ->
                                new ResourcePermission(resource, storeFactory
                                        .getResourceServerStore()
                                        .findById(resource.getResourceServer().getId()),
                                        new HashMap<>())).collect(Collectors.toList()),
                        new DefaultEvaluationContext(
                                new UserModelIdentity(realmModel, userModel), session));
        AuthorizationRequest authorizationRequest = new AuthorizationRequest();
        RadiusEvaluationDecisionCollector permissionCollector = permissionEvaluator
                .evaluate(new RadiusEvaluationDecisionCollector(authorizationProvider,
                        resourceServer, authorizationRequest));
        Collection<Result> results = permissionCollector.getResults();
        return results.stream().filter(result -> Decision.Effect
                .PERMIT == result.getEffect()).map(result -> result
                .getPermission().getResource()).collect(Collectors.toSet());
    }

    @Override
    protected Set<Resource> getKeycloakTypes() {
        IRadiusUserInfo radiusUserInfo = radiusUserInfoGetter.getRadiusUserInfo();
        UserModel userModel = radiusUserInfo.getUserModel();
        RealmModel realmModel = radiusUserInfo.getRealmModel();
        ClientModel clientModel = radiusUserInfo.getClientModel();
        AuthorizationProvider authorizationProvider = session
                .getProvider(AuthorizationProvider.class);
        ResourceServer resourceServer = authorizationProvider.getStoreFactory()
                .getResourceServerStore().findById(clientModel.getId());
        return resourceServer != null ?
                getKeycloakTypes(authorizationProvider, resourceServer, userModel, realmModel) :
                Collections.EMPTY_SET;
    }

    @Override
    protected Map<String, Set<String>> getAttributes(
            Resource resource) {
        Map<String, Set<String>> collect = resource
                .getAttributes().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue() != null ?
                                new HashSet<>(entry.getValue()) : new HashSet<>()));
        return filter(collect);
    }
}
