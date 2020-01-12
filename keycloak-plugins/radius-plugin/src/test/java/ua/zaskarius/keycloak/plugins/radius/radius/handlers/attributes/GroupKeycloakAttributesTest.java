package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import org.keycloak.models.GroupModel;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.attribute.AttributeType;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals.AttributeConditional;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.util.*;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class GroupKeycloakAttributesTest extends AbstractRadiusTest {
    GroupKeycloakAttributes groupKeycloakAttributes;
    @Mock
    private GroupModel groupModel;

    @Mock
    private Dictionary dictionary;

    private AttributeType attributeType;

    @BeforeMethod
    public void beforeMethods() {
        reset(groupModel);
        groupKeycloakAttributes =
                new GroupKeycloakAttributes(session);
        when(userModel.getGroups()).thenReturn(
                new HashSet<>(Collections.singletonList(groupModel)));
        HashMap<String, List<String>> map = new HashMap<>();
        map.put("testAttribute", Arrays.asList("0002", "0004"));
        map.put("testAttribute2", Collections.emptyList());
        map.put("testAttribute3", null);

        when(groupModel.getAttributes()).thenReturn(map);
        attributeType = new AttributeType(0, 1,
                "testAttribute", "string");
        when(dictionary.getAttributeTypeByName("testAttribute"))
                .thenReturn(attributeType);
        when(dictionary.getAttributeTypeByName("Service-Type"))
                .thenReturn(attributeType);
    }

    @Test
    public void testMethods() {
        assertEquals(groupKeycloakAttributes.getType(), KeycloakAttributesType.GROUP);
        List<AttributeConditional<GroupModel>> attributeConditional =
                groupKeycloakAttributes.getAttributeConditional();
        assertNotNull(attributeConditional);
        assertEquals(attributeConditional.size(), 1);
        Set<GroupModel> keycloakTypes = groupKeycloakAttributes
                .getKeycloakTypes();
        assertNotNull(keycloakTypes);
        assertEquals(keycloakTypes.iterator().next(), groupModel);
    }

    @Test
    public void getAttributes() {
        Map<String, Set<String>> attributes = groupKeycloakAttributes
                .getAttributes(groupModel);
        assertNotNull(attributes);
        assertEquals(attributes.size(), 3);
    }



    @Test
    public void getRead() {
        AccessRequest accessRequest = new AccessRequest(dictionary, 1, new byte[16]);
        accessRequest.addAttribute(attributeType.create(dictionary, "test"));
        KeycloakAttributes keycloakAttributes = groupKeycloakAttributes.read();
        keycloakAttributes.ignoreUndefinedAttributes(dictionary);
        keycloakAttributes.filter(accessRequest);
        RadiusPacket answer = new RadiusPacket(dictionary, 2, 1);
        keycloakAttributes.fillAnswer(answer);
        assertNotNull(answer.getAttributes());
    }

}
