package ua.zaskarius.keycloak.plugins.radius.event;

import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.keycloak.events.admin.AdminEvent;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class AuthentificatorConfigActionTest extends AbstractRadiusTest {
    private AuthentificatorConfigAction authentificatorConfigAction =
            new AuthentificatorConfigAction();

    @Test
    public void testgetAuthConfigId() {

        AdminEvent adminEvent = new AdminEvent();
        adminEvent.setResourcePath("authentication/config/id");
        String authConfigId = authentificatorConfigAction
                .getAuthConfigId(session, adminEvent, realmModel);
        assertEquals(authConfigId, "id");
    }

    @Test
    public void testgetAuthConfigIdDiff() {

        AdminEvent adminEvent = new AdminEvent();
        adminEvent.setResourcePath("authentication/test/id");
        String authConfigId = authentificatorConfigAction
                .getAuthConfigId(session, adminEvent, realmModel);
        assertEquals(authConfigId, "");
    }

}
