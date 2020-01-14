package ua.zaskarius.keycloak.plugins.radius.event.log;

import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import static org.testng.Assert.assertNotNull;

public class EventLoggerUtilsTest extends AbstractRadiusTest {
    @Test
    public void testEventLoggerFactory() {
        assertNotNull(EventLoggerUtils.createEvent(session, realmModel, clientConnection));
    }

    @Test
    public void testEventLoggerFactory2() {
        assertNotNull(EventLoggerUtils.createEvent(session, realmModel, clientModel, clientConnection));
    }

    @Test
    public void testMasterEventLoggerFactory() {
        assertNotNull(EventLoggerUtils.createMasterEvent(session, clientConnection));
    }
}
