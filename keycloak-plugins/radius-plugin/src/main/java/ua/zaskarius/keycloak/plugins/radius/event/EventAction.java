package ua.zaskarius.keycloak.plugins.radius.event;

import org.keycloak.events.Event;
import org.keycloak.models.KeycloakSession;

public interface EventAction {
    void invokeAction(KeycloakSession keycloakSession, Event event);
}
