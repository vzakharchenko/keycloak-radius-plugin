package com.github.vzakharchenko.radius.radius.handlers.attributes;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.keycloak.models.RoleModel;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.attribute.AttributeType;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;

import java.util.*;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RoleKeycloakAttributesTest extends AbstractRadiusTest {
    RoleKeycloakAttributes roleKeycloakAttributes;
    @Mock
    private RoleModel roleModel;

    @Mock
    private Dictionary dictionary;

    private AttributeType attributeType;

    private AccessRequest accessRequest;

    @BeforeMethod
    public void beforeMethods() {
        reset(roleModel);
        reset(dictionary);
        accessRequest = new AccessRequest(dictionary, 1, new byte[16]);
        roleKeycloakAttributes =
                new RoleKeycloakAttributes(session, accessRequest);
        when(userModel.getRoleMappings()).thenReturn(
                new HashSet<>(Collections.singletonList(roleModel)));
        HashMap<String, List<String>> map = new HashMap<>();
        map.put("testAttribute", Arrays.asList("0002", "004b"));
        map.put("testAttribute2", Collections.emptyList());
        map.put("testAttribute3", null);
        when(roleModel.getAttributes()).thenReturn(map);
        attributeType = new AttributeType(0, 1,
                "testAttribute", "string");
        when(dictionary.getAttributeTypeByName("testAttribute"))
                .thenReturn(attributeType);
        when(dictionary.getAttributeTypeByName("Service-Type"))
                .thenReturn(attributeType);
    }

    @Test
    public void testMethods() {
        assertEquals(roleKeycloakAttributes.getType(), KeycloakAttributesType.ROLE);
        Set<RoleModel> keycloakTypes = roleKeycloakAttributes
                .getKeycloakTypes();
        assertNotNull(keycloakTypes);
        assertEquals(keycloakTypes.iterator().next(), roleModel);
    }

    @Test
    public void getAttributes() {
        Map<String, Set<String>> attributes = roleKeycloakAttributes
                .getAttributes(roleModel);
        assertNotNull(attributes);
        assertEquals(attributes.size(), 3);

    }

    @Test
    public void getRead() {
        AccessRequest accessRequest = new AccessRequest(dictionary, 1, new byte[16]);
        accessRequest.addAttribute(attributeType.create(dictionary, "test"));
        KeycloakAttributes keycloakAttributes = roleKeycloakAttributes.read();
        keycloakAttributes.ignoreUndefinedAttributes(dictionary);
        RadiusPacket answer = new RadiusPacket(dictionary, 2, 1);
        keycloakAttributes.fillAnswer(answer);
        assertNotNull(answer.getAttributes());
    }

}
