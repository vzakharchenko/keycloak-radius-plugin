package com.github.vzakharchenko.radius.radius.handlers.attributes.permissions;

import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.model.Scope;
import org.keycloak.authorization.permission.ResourcePermission;
import org.keycloak.authorization.policy.evaluation.DecisionPermissionCollector;
import org.keycloak.authorization.policy.evaluation.Result;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.Permission;

import java.util.Collection;
import java.util.Set;

public class RadiusEvaluationDecisionCollector extends DecisionPermissionCollector {

    public RadiusEvaluationDecisionCollector(AuthorizationProvider authorizationProvider,
                                             ResourceServer resourceServer,
                                             AuthorizationRequest request) {
        super(authorizationProvider, resourceServer, request);
    }

    @Override
    protected boolean isGranted(Result.PolicyResult policyResult) {
        if (super.isGranted(policyResult)) {
            policyResult.setEffect(Effect.PERMIT);
            return true;
        }
        return false;
    }

    //CHECKSTYLE:OFF
    @Override
    protected void grantPermission(AuthorizationProvider authorizationProvider,
                                   Set<Permission> permissions,
                                   ResourcePermission permission,
                                   Collection<Scope> grantedScopes,
                                   ResourceServer resourceServer,
                                   AuthorizationRequest request,
                                   Result result) {
        //CHECKSTYLE:ON
        result.setStatus(Effect.PERMIT);
        result.getPermission().getScopes().retainAll(grantedScopes);
        super.grantPermission(authorizationProvider, permissions,
                permission, grantedScopes, resourceServer, request, result);
    }

    public Collection<Result> getResults() {
        return results.values();
    }
}
