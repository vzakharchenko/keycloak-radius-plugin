package com.github.vzakharchenko.radius.radius.handlers.attributes;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.keycloak.models.GroupModel;
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

public class GroupKeycloakAttributesTest extends AbstractRadiusTest {
    GroupKeycloakAttributes groupKeycloakAttributes;
    @Mock
    private GroupModel groupModel;

    @Mock
    private Dictionary dictionary;

    private AttributeType attributeType;

    private AccessRequest accessRequest;

    @BeforeMethod
    public void beforeMethods() {
        accessRequest = new AccessRequest(dictionary, 1, new byte[16]);
        reset(groupModel);
        groupKeycloakAttributes =
                new GroupKeycloakAttributes(session, accessRequest);
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
        RadiusPacket answer = new RadiusPacket(dictionary, 2, 1);
        keycloakAttributes.fillAnswer(answer);
        assertNotNull(answer.getAttributes());
    }

}
