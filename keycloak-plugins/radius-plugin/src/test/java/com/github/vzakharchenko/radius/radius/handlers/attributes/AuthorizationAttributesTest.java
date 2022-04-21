package com.github.vzakharchenko.radius.radius.handlers.attributes;

import com.github.vzakharchenko.radius.radius.handlers.attributes.permissions.RadiusEvaluationDecisionCollector;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.keycloak.authorization.Decision;
import org.keycloak.authorization.model.Resource;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.permission.ResourcePermission;
import org.keycloak.authorization.policy.evaluation.AbstractDecisionCollector;
import org.keycloak.authorization.policy.evaluation.Evaluation;
import org.keycloak.authorization.policy.evaluation.Result;
import org.keycloak.representations.idm.authorization.Permission;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.attribute.AttributeType;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.AccessRequest;

import java.lang.reflect.Field;
import java.util.*;

import static com.github.vzakharchenko.radius.radius.handlers.attributes.KeycloakAttributesType.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class AuthorizationAttributesTest extends AbstractRadiusTest {

    private AuthorizationAttributes authorizationAttributes;

    @Mock
    private ResourceServer resourceServer;

    @Mock
    private Dictionary dictionary;

    private AttributeType attributeType;
    private AccessRequest accessRequest;

    @Mock
    private Resource resource;

    @Mock
    private Evaluation evaluation;

    private ResourcePermission resourcePermission;


    @BeforeMethod
    public void beforeMethods() {
        reset(dictionary);
        reset(resourceServer);
        reset(resource);
        reset(evaluation);
        when(resourceServer.getId()).thenReturn(CLIENT_ID);
        when(resource.getResourceServer()).thenReturn(resourceServer);
        when(resource.getResourceServer().getId()).thenReturn(CLIENT_ID);
        accessRequest = new AccessRequest(dictionary, 1, new byte[16]);
        authorizationAttributes = new AuthorizationAttributes(session, accessRequest);
        HashMap<String, List<String>> map = new HashMap<>();
        map.put("testAttribute", Arrays.asList("0000", "0001"));
        map.put("testAttribute2", Collections.emptyList());
        map.put("testAttribute3", null);
        when(userModel.getAttributes()).thenReturn(map);
        attributeType = new AttributeType(0, 1,
                "testAttribute", "string");
        when(dictionary.getAttributeTypeByName("testAttribute"))
                .thenReturn(attributeType);
        when(dictionary.getAttributeTypeByName("Service-Type"))
                .thenReturn(attributeType);
        RadiusAttribute radiusAttribute = attributeType.create(dictionary, "0");
        accessRequest.addAttribute(radiusAttribute);

        when(resourceStore.findByResourceServer(resourceServer))
                .thenReturn(Arrays.asList(resource));
        when(resourceServerStore.findById(CLIENT_ID)).thenReturn(resourceServer);
        resourcePermission = new ResourcePermission(resource, resourceServer, new HashMap<>());

        doAnswer(invocationOnMock -> {
            RadiusEvaluationDecisionCollector decisionCollector = invocationOnMock.getArgument(3, RadiusEvaluationDecisionCollector.class);
            Permission permission = new Permission();
            permission.setResourceId("resourceId");
            permission.setResourceName("resourceName");
            decisionCollector.results().add(permission);
            Field field = AbstractDecisionCollector.class.getDeclaredField("results");
            field.setAccessible(true);
            Map<ResourcePermission, Result> results = (Map<ResourcePermission, Result>) field.get(decisionCollector);
            Result result = new Result(resourcePermission, evaluation);
            result.setStatus(Decision.Effect.PERMIT);
            results.put(resourcePermission, result);
            return Void.class;
        }).when(policyEvaluator).evaluate(any(), any(), any(), any(), any());
        when(resource.getAttributes()).thenReturn(map);
    }

    @Test
    public void testMethods() {
        assertEquals(authorizationAttributes.getType(), AUTHORIZATION);
    }

    @Test
    public void testGetKeycloakTypes() {
        Set<Resource> keycloakTypes = authorizationAttributes
                .getKeycloakTypes(authorizationProvider, resourceServer, userModel, realmModel);
        assertNotNull(keycloakTypes);
        assertEquals(keycloakTypes.size(), 1);
    }

    @Test
    public void testGetKeycloakTypes1() {
        Set<Resource> keycloakTypes = authorizationAttributes
                .getKeycloakTypes();
        assertNotNull(keycloakTypes);
        assertEquals(keycloakTypes.size(), 1);
    }

    @Test
    public void testGetKeycloakTypes0() {
        when(resourceServerStore.findById(CLIENT_ID)).thenReturn(null);
        Set<Resource> keycloakTypes = authorizationAttributes
                .getKeycloakTypes();
        assertNotNull(keycloakTypes);
        assertEquals(keycloakTypes.size(), 0);
    }

    @Test
    public void testGetAttributes() {
        assertNotNull(authorizationAttributes.getAttributes(resource));
    }
}
