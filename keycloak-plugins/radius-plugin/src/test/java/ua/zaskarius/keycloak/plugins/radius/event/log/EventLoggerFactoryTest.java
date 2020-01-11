package ua.zaskarius.keycloak.plugins.radius.event.log;

import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import static org.testng.Assert.assertNotNull;

public class EventLoggerFactoryTest extends AbstractRadiusTest {
    @Test
    public void testEventLoggerFactory() {
        assertNotNull(EventLoggerFactory.createEvent(session, realmModel, clientConnection));
    }

    @Test
    public void testEventLoggerFactory2() {
        assertNotNull(EventLoggerFactory.createEvent(session, realmModel, clientModel, clientConnection));
    }

    @Test
    public void testMasterEventLoggerFactory() {
        assertNotNull(EventLoggerFactory.createMasterEvent(session, clientConnection));
    }
}
