package ua.zaskarius.keycloak.plugins.radius.event;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.KeycloakSession;

import java.lang.reflect.Constructor;
import java.util.Map;

public abstract class AbstractRadiusEventProvider implements EventListenerProvider {

    private final KeycloakSession session;

    public AbstractRadiusEventProvider(KeycloakSession session) {
        this.session = session;
    }

    protected abstract Map<EventType, Class<? extends EventAction>> getEventActionMap();

    protected abstract Map<ResourceType,
            Class<? extends AdminEventAction>> getAdminEventActionMap();

    @Override
    public void onEvent(Event event) {
        Class<? extends EventAction> eventActionClass = getEventActionMap().get(event.getType());
        if (eventActionClass != null) {
            try {
                Constructor<? extends EventAction> constructor = eventActionClass
                        .getConstructor();
                EventAction eventAction = constructor
                        .newInstance();
                eventAction.invokeAction(session, event);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        ResourceType resourceType = event.getResourceType();


        if (resourceType != null) {
            Class<? extends AdminEventAction> eventActionClass = getAdminEventActionMap()
                    .get(resourceType);
            if (eventActionClass != null) {
                try {
                    Constructor<? extends AdminEventAction> constructor = eventActionClass
                            .getConstructor();
                    AdminEventAction eventAction = constructor
                            .newInstance();
                    eventAction.invokeAction(session, event);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }
}
