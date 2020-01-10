package ua.zaskarius.keycloak.plugins.radius.event;

import ua.zaskarius.keycloak.plugins.radius.configuration.RadiusConfigHelper;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class AuthentificatorExecutionAction extends AbstractAuthentificatorAction {

    @Override
    protected String getAuthConfigId(KeycloakSession session, AdminEvent event, RealmModel realm) {
        String s = event
                .getResourcePath()
                .replaceFirst("^authentication/executions/", "");
        if (event
                .getResourcePath().contains("authentication/executions") &&
                s.endsWith("config") && event.getOperationType() == OperationType.CREATE) {
            return RadiusConfigHelper.getConfig().getCommonSettings(realm).getId();
        } else {
            return "";
        }
    }
}
