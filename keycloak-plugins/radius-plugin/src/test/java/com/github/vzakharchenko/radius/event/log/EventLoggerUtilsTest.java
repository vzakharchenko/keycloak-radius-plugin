package com.github.vzakharchenko.radius.event.log;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

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
