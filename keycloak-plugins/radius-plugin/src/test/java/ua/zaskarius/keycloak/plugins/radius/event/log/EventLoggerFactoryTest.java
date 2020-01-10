package ua.zaskarius.keycloak.plugins.radius.event.log;

import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class EventLoggerFactoryTest extends AbstractRadiusTest {
    @Test
    public void testEventLoggerFactory() {
        assertNotNull(EventLoggerFactory.createEvent(session, realmModel, clientConnection));
    }
}
