package ua.zaskarius.keycloak.plugins.radius.event;

import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class AuthentificatorExecutionActionTest extends AbstractRadiusTest {
    private AuthentificatorExecutionAction authentificatorExecutionAction =
            new AuthentificatorExecutionAction();

    @Test
    public void testgetAuthConfigId() {

        AdminEvent adminEvent = new AdminEvent();
        adminEvent.setResourcePath("authentication/executions/execId/config");
        adminEvent.setOperationType(OperationType.CREATE);
        String authConfigId = authentificatorExecutionAction
                .getAuthConfigId(session, adminEvent, realmModel);
        assertEquals(authConfigId, "id");
    }

    @Test
    public void testgetAuthConfigIdDiff() {

        AdminEvent adminEvent = new AdminEvent();
        adminEvent.setResourcePath("authentication/test/execId/config");
        adminEvent.setOperationType(OperationType.CREATE);
        String authConfigId = authentificatorExecutionAction
                .getAuthConfigId(session, adminEvent, realmModel);
        assertEquals(authConfigId, "");
    }

    @Test
    public void testgetAuthConfigIdOperation() {

        AdminEvent adminEvent = new AdminEvent();
        adminEvent.setResourcePath("authentication/executions/execId/config");
        adminEvent.setOperationType(OperationType.UPDATE);
        String authConfigId = authentificatorExecutionAction
                .getAuthConfigId(session, adminEvent, realmModel);
        assertEquals(authConfigId, "");
    }

    @Test
    public void testgetAuthConfigIdOperationConfig() {

        AdminEvent adminEvent = new AdminEvent();
        adminEvent.setResourcePath("authentication/executions/execId");
        adminEvent.setOperationType(OperationType.UPDATE);
        String authConfigId = authentificatorExecutionAction
                .getAuthConfigId(session, adminEvent, realmModel);
        assertEquals(authConfigId, "");
    }
}
