package ua.zaskarius.keycloak.plugins.radius.event;

import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

public interface AdminEventAction {
    void invokeAction(KeycloakSession keycloakSession, AdminEvent event);
}
