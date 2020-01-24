package com.github.vzakharchenko.radius.radius.handlers.attributes;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
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

    private AccessRequest accessRequest;

    @BeforeMethod
    public void beforeMethods() {
        reset(dictionary);
        accessRequest = new AccessRequest(dictionary, 1, new byte[16]);
        userKeycloakAttributes =
                new UserKeycloakAttributes(session, accessRequest);
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
    }

    @Test
    public void testMethods() {
        assertEquals(userKeycloakAttributes.getType(), KeycloakAttributesType.USER);
        Set<UserModel> keycloakTypes = userKeycloakAttributes
                .getKeycloakTypes();
        assertNotNull(keycloakTypes);
        assertEquals(keycloakTypes.iterator().next(), userModel);
    }

    @Test
    public void getAttributes() {
        Map<String, Set<String>> attributes = userKeycloakAttributes
                .getAttributes(userModel);
        assertNotNull(attributes);
        assertEquals(attributes.size(), 3);

    }

    @Test
    public void getRead() {
        AccessRequest accessRequest = new AccessRequest(dictionary, 1, new byte[16]);
        accessRequest.addAttribute(attributeType.create(dictionary, "test"));
        KeycloakAttributes keycloakAttributes = userKeycloakAttributes.read();
        keycloakAttributes.ignoreUndefinedAttributes(dictionary);
        RadiusPacket answer = new RadiusPacket(dictionary, 2, 1);
        keycloakAttributes.fillAnswer(answer);
        assertNotNull(answer.getAttributes());
    }


}
