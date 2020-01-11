package ua.zaskarius.keycloak.plugins.radius.event.log;

import org.keycloak.models.ClientModel;
import ua.zaskarius.keycloak.plugins.radius.configuration.RadiusConfigHelper;
import org.keycloak.Config;
import org.keycloak.common.ClientConnection;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public final class EventLoggerFactory {

    public static final String RADIUS = "RADIUS";
    public static final String RADIUS_HOST = "RADIUS_HOST";
    public static final String RADIUS_MESSAGE = "RADIUS_MESSAGE";

    private EventLoggerFactory() {
    }

    public static EventBuilder createMasterEvent(
            KeycloakSession session,
            ClientConnection clientConnection) {

        return createEvent(session, session.realms().getRealm(Config.getAdminRealm()),
                clientConnection);
    }

    public static EventBuilder createEvent(
            KeycloakSession session,
            RealmModel realmModel,
            ClientConnection clientConnection) {

        return new EventBuilder(realmModel, session, clientConnection)
                .detail(RADIUS,
                RadiusConfigHelper.getConfig().getRadiusSettings(session).getProvider())
                .detail(RADIUS_HOST,
                        clientConnection.getRemoteAddr());
    }


    public static EventBuilder createEvent(
            KeycloakSession session,
            RealmModel realmModel,
            ClientModel clientModel,
            ClientConnection clientConnection) {

        return new EventBuilder(realmModel, session, clientConnection)
                .detail(RADIUS,
                RadiusConfigHelper.getConfig().getRadiusSettings(session).getProvider())
                .client(clientModel)
                .detail(RADIUS_HOST,
                        clientConnection.getRemoteAddr());
    }
}
