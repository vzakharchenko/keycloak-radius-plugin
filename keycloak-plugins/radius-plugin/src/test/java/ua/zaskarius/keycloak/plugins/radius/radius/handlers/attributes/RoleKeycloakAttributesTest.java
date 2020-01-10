package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals.AttributeConditional;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
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

    @BeforeMethod
    public void beforeMethods() {
        reset(roleModel);
        reset(dictionary);
        roleKeycloakAttributes =
                new RoleKeycloakAttributes(session);
        when(userModel.getRoleMappings()).thenReturn(
                new HashSet<>(Collections.singletonList(roleModel)));
        HashMap<String, List<String>> map = new HashMap<>();
        map.put("testAttribute", Arrays.asList("v1", "v2"));
        map.put("testAttribute2", Collections.emptyList());
        map.put("testAttribute3", null);
        map.put(AbstractKeycloakAttributes.RADIUS_ATTRIBUTES, Arrays.asList("testAttribute", "testAttribute2"));
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
        List<AttributeConditional<RoleModel>> attributeConditional =
                roleKeycloakAttributes.getAttributeConditional();
        assertNotNull(attributeConditional);
        assertEquals(attributeConditional.size(), 1);
        Set<RoleModel> keycloakTypes = roleKeycloakAttributes
                .getKeycloakTypes();
        assertNotNull(keycloakTypes);
        assertEquals(keycloakTypes.iterator().next(), roleModel);
    }

    @Test
    public void getAttributes() {
        List<String> testattribute = roleKeycloakAttributes
                .getAttributes(roleModel, "testattribute");
        assertNotNull(testattribute);
        assertEquals(testattribute.size(), 2);
        testattribute = roleKeycloakAttributes
                .getAttributes(roleModel, "testAttribute2");
        assertNotNull(testattribute);
        assertEquals(testattribute.size(), 0);
        testattribute = roleKeycloakAttributes
                .getAttributes(roleModel, "testAttribute3");
        assertNotNull(testattribute);
        assertEquals(testattribute.size(), 0);

        testattribute = roleKeycloakAttributes
                .getAttributes(roleModel, "testAttribute4");
        assertNotNull(testattribute);
        assertEquals(testattribute.size(), 0);

    }

    @Test
    public void getRead() {
        AccessRequest accessRequest = new AccessRequest(dictionary, 1, new byte[16]);
        accessRequest.addAttribute(attributeType.create(dictionary, "test"));
        KeycloakAttributes keycloakAttributes = roleKeycloakAttributes.read();
        keycloakAttributes.ignoreUndefinedAttributes(dictionary);
        keycloakAttributes.filter(accessRequest);
        RadiusPacket answer = new RadiusPacket(dictionary, 2, 1);
        keycloakAttributes.fillAnswer(answer);
        assertNotNull(answer.getAttributes());
    }

}
