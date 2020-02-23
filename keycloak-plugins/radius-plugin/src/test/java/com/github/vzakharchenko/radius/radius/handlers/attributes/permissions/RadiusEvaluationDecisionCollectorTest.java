package com.github.vzakharchenko.radius.radius.handlers.attributes.permissions;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.keycloak.authorization.Decision;
import org.keycloak.authorization.model.Policy;
import org.keycloak.authorization.model.Resource;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.permission.ResourcePermission;
import org.keycloak.authorization.policy.evaluation.Evaluation;
import org.keycloak.authorization.policy.evaluation.Result;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.Permission;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.keycloak.representations.idm.authorization.DecisionStrategy.AFFIRMATIVE;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class RadiusEvaluationDecisionCollectorTest extends AbstractRadiusTest {
    private RadiusEvaluationDecisionCollector radiusEvaluationDecisionCollector;

    @Mock
    private ResourceServer resourceServer;

    @Mock
    private Policy policy;

    @Mock
    private Resource resource;

    @Mock
    private Evaluation evaluation;

    @BeforeMethod
    public void beforeMethods() {
        reset(resourceServer);
        reset(policy);
        reset(resource);
        reset(evaluation);
        radiusEvaluationDecisionCollector = new RadiusEvaluationDecisionCollector(
                authorizationProvider, resourceServer, new AuthorizationRequest());
        when(policy.getDecisionStrategy()).thenReturn(AFFIRMATIVE);

    }

    @Test
    public void testIsGranted() {
        Result.PolicyResult policyResult = new Result.PolicyResult(policy);
        policyResult.setEffect(Decision.Effect.PERMIT);
        policyResult.policy(policy, Decision.Effect.PERMIT);
        assertTrue(radiusEvaluationDecisionCollector.isGranted(policyResult));
    }

    @Test
    public void testIsDeny() {
        Result.PolicyResult policyResult = new Result.PolicyResult(policy);
        policyResult.setEffect(Decision.Effect.DENY);
        policyResult.policy(policy, Decision.Effect.DENY);
        assertFalse(radiusEvaluationDecisionCollector.isGranted(policyResult));
    }

    @Test
    public void testEmpty() {
        assertEquals(radiusEvaluationDecisionCollector.getResults().size(), 0);
    }

    @Test
    public void testGrantPermission() {
        ResourcePermission resourcePermission = new ResourcePermission(resource, Arrays.asList(), resourceServer);
        Result result = new Result(resourcePermission, evaluation);
        ArrayList<Permission> permissions = new ArrayList<>();
        radiusEvaluationDecisionCollector.grantPermission(authorizationProvider,
                permissions,
                resourcePermission,
                Collections.emptyList(),
                resourceServer,
                new AuthorizationRequest(), result);
        assertEquals(permissions.size(),1);
    }
}
