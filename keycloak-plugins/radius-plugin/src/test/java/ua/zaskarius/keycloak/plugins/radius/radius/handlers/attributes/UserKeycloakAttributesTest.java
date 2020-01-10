package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals.AttributeConditional;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.keycloak.models.UserModel;
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

public class UserKeycloakAttributesTest extends AbstractRadiusTest {
    UserKeycloakAttributes userKeycloakAttributes;

    @Mock
    private Dictionary dictionary;

    private AttributeType attributeType;

    @BeforeMethod
    public void beforeMethods() {
        reset(dictionary);
        userKeycloakAttributes =
                new UserKeycloakAttributes(session);
        HashMap<String, List<String>> map = new HashMap<>();
        map.put("testAttribute", Arrays.asList("v1", "v2"));
        map.put("testAttribute2", Collections.emptyList());
        map.put("testAttribute3", null);
        map.put(AbstractKeycloakAttributes.RADIUS_ATTRIBUTES, Arrays.asList("testAttribute", "testAttribute2"));
        when(userModel.getAttributes()).thenReturn(map);
        attributeType = new AttributeType(0, 1,
                "testAttribute", "string");
        when(dictionary.getAttributeTypeByName("testAttribute"))
                .thenReturn(attributeType);
        when(dictionary.getAttributeTypeByName("Service-Type"))
                .thenReturn(attributeType);
    }

    @Test
    public void testMethods() {
        assertEquals(userKeycloakAttributes.getType(), KeycloakAttributesType.USER);
        List<AttributeConditional<UserModel>> attributeConditional =
                userKeycloakAttributes.getAttributeConditional();
        assertNotNull(attributeConditional);
        assertEquals(attributeConditional.size(), 0);
        Set<UserModel> keycloakTypes = userKeycloakAttributes
                .getKeycloakTypes();
        assertNotNull(keycloakTypes);
        assertEquals(keycloakTypes.iterator().next(), userModel);
    }

    @Test
    public void getAttributes() {
        List<String> testattribute = userKeycloakAttributes
                .getAttributes(userModel, "testattribute");
        assertNotNull(testattribute);
        assertEquals(testattribute.size(), 2);
        testattribute = userKeycloakAttributes
                .getAttributes(userModel, "testAttribute2");
        assertNotNull(testattribute);
        assertEquals(testattribute.size(), 0);
        testattribute = userKeycloakAttributes
                .getAttributes(userModel, "testAttribute3");
        assertNotNull(testattribute);
        assertEquals(testattribute.size(), 0);

        testattribute = userKeycloakAttributes
                .getAttributes(userModel, "testAttribute4");
        assertNotNull(testattribute);
        assertEquals(testattribute.size(), 0);

    }


}
