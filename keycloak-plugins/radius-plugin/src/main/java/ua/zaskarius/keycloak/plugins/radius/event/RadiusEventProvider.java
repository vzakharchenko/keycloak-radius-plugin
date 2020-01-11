package ua.zaskarius.keycloak.plugins.radius.event;

import org.keycloak.events.EventType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.KeycloakSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RadiusEventProvider extends AbstractRadiusEventProvider {

    private static final Map<EventType,
            Class<? extends EventAction>> EVENT_ACTION_MAP =
            new ConcurrentHashMap<>();


    private final Map<ResourceType,
            Class<? extends AdminEventAction>> adminEventActionMap =
            new ConcurrentHashMap<>();

    public RadiusEventProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    protected Map<EventType, Class<? extends EventAction>> getEventActionMap() {
        return EVENT_ACTION_MAP;
    }

    @Override
    protected Map<ResourceType,
            Class<? extends AdminEventAction>> getAdminEventActionMap() {
        return adminEventActionMap;
    }


    @Override
    public void close() {

    }
}
