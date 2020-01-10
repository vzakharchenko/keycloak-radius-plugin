package ua.zaskarius.keycloak.plugins.radius.event;

import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RadiusEventListenerProviderFactoryTest extends AbstractRadiusTest {
    private RadiusEventListenerProviderFactory
            radiusEventListenerProviderFactory
            = new RadiusEventListenerProviderFactory();

    @Test
    public void testMethods() {
        assertEquals(radiusEventListenerProviderFactory.getId(), RadiusEventListenerProviderFactory.RADIUS_EVENT_LISTENER);
        assertEquals(radiusEventListenerProviderFactory.getOperationalInfo().size(), 0);
        radiusEventListenerProviderFactory.close();
        radiusEventListenerProviderFactory.init(null);
        radiusEventListenerProviderFactory.postInit(null);
        assertNotNull(radiusEventListenerProviderFactory.create(session));
    }
}
