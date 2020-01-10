package ua.zaskarius.keycloak.plugins.radius.event;

import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class AuthentificatorConfigAction extends AbstractAuthentificatorAction {


    @Override
    protected String getAuthConfigId(KeycloakSession session, AdminEvent event, RealmModel realm) {
        return event
                .getResourcePath()
                .contains("authentication/config") ?
                event
                        .getResourcePath()
                        .replaceFirst("^authentication/config/", "") : "";
    }
}
