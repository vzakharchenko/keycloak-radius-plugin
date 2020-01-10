package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class KeycloakAttributesFactoryTest extends AbstractRadiusTest {
    private KeycloakAttributesFactory keycloakAttributesFactory =
            new KeycloakAttributesFactory();

    @Test
    public void methodTests() {
        keycloakAttributesFactory.close();
        keycloakAttributesFactory.init(null);
        keycloakAttributesFactory.postInit(null);
        assertNotNull(keycloakAttributesFactory.create(session));
        assertEquals(keycloakAttributesFactory.getId(), KeycloakAttributesFactory.KEYCLOAK_ATTRIBUTES_DEFAULT);

    }

    @Test
    public void createKeycloakAttributesUserTest() {
        KeycloakAttributes keycloakAttributes = keycloakAttributesFactory
                .createKeycloakAttributes(session, KeycloakAttributesType.USER);
        assertTrue(keycloakAttributes instanceof UserKeycloakAttributes);
    }

    @Test
    public void createKeycloakAttributesRoleTest() {
        KeycloakAttributes keycloakAttributes = keycloakAttributesFactory
                .createKeycloakAttributes(session, KeycloakAttributesType.ROLE);
        assertTrue(keycloakAttributes instanceof RoleKeycloakAttributes);
    }

    @Test
    public void createKeycloakAttributesGroupTest() {
        KeycloakAttributes keycloakAttributes = keycloakAttributesFactory
                .createKeycloakAttributes(session, KeycloakAttributesType.GROUP);
        assertTrue(keycloakAttributes instanceof GroupKeycloakAttributes);
    }

}
