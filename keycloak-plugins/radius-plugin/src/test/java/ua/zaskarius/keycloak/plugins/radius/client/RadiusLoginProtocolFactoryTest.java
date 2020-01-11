package ua.zaskarius.keycloak.plugins.radius.client;

import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import static org.testng.Assert.*;
import static ua.zaskarius.keycloak.plugins.radius.client.RadiusLoginProtocolFactory.RADIUS_PROTOCOL;

public class RadiusLoginProtocolFactoryTest extends AbstractRadiusTest {
    private RadiusLoginProtocolFactory radiusLoginProtocolFactory = new RadiusLoginProtocolFactory();

    @Test
    public void testMethods() {
        radiusLoginProtocolFactory.setupClientDefaults(null, null);
        radiusLoginProtocolFactory.createDefaultClientScopes(null, false);
        radiusLoginProtocolFactory.close();
        radiusLoginProtocolFactory.init(null);
        radiusLoginProtocolFactory.postInit(keycloakSessionFactory);
        assertNull(radiusLoginProtocolFactory.createProtocolEndpoint(null, null));
        assertNotNull(radiusLoginProtocolFactory.create(session));
        assertEquals(radiusLoginProtocolFactory.getBuiltinMappers().size(), 0);
        assertEquals(radiusLoginProtocolFactory.getId(), RADIUS_PROTOCOL);


    }
}
