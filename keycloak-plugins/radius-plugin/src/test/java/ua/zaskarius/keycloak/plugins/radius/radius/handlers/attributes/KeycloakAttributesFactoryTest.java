package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import org.testng.annotations.Test;
import org.tinyradius.packet.AccessRequest;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import static org.testng.Assert.*;

public class KeycloakAttributesFactoryTest extends AbstractRadiusTest {
    private KeycloakAttributesFactory keycloakAttributesFactory =
            new KeycloakAttributesFactory();
    private AccessRequest accessRequest;

    @Test
    public void methodTests() {
        accessRequest = new AccessRequest(realDictionary, 1, new byte[16]);
        keycloakAttributesFactory.close();
        keycloakAttributesFactory.init(null);
        keycloakAttributesFactory.postInit(null);
        assertNotNull(keycloakAttributesFactory.create(session));
        assertEquals(keycloakAttributesFactory.getId(), KeycloakAttributesFactory.KEYCLOAK_ATTRIBUTES_DEFAULT);

    }

    @Test
    public void createKeycloakAttributesUserTest() {
        KeycloakAttributes keycloakAttributes = keycloakAttributesFactory
                .createKeycloakAttributes(accessRequest, session, KeycloakAttributesType.USER);
        assertTrue(keycloakAttributes instanceof UserKeycloakAttributes);
    }

    @Test
    public void createKeycloakAttributesRoleTest() {
        KeycloakAttributes keycloakAttributes = keycloakAttributesFactory
                .createKeycloakAttributes(accessRequest, session, KeycloakAttributesType.ROLE);
        assertTrue(keycloakAttributes instanceof RoleKeycloakAttributes);
    }

    @Test
    public void createKeycloakAttributesGroupTest() {
        KeycloakAttributes keycloakAttributes = keycloakAttributesFactory
                .createKeycloakAttributes(accessRequest, session, KeycloakAttributesType.GROUP);
        assertTrue(keycloakAttributes instanceof GroupKeycloakAttributes);
    }

}
