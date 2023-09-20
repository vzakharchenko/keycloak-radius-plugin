package com.github.vzakharchenko.radius.event.log;

import org.keycloak.Config;
import org.keycloak.common.ClientConnection;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public final class EventLoggerUtils {

    public static final String RADIUS = "RADIUS";
    public static final String RADIUS_HOST = "RADIUS_HOST";
    public static final String RADIUS_MESSAGE = "RADIUS_MESSAGE";

    private EventLoggerUtils() {
    }

    public static EventBuilder createMasterEvent(
            KeycloakSession session,
            ClientConnection clientConnection) {
        return createEvent(session, session.realms().getRealmByName(Config.getAdminRealm()),
                clientConnection);
    }

    public static EventBuilder createEvent(
            KeycloakSession session,
            RealmModel realmModel,
            ClientConnection clientConnection) {

        return new EventBuilder(realmModel, session, clientConnection)
                .detail(RADIUS,
                        "Radius connection without client")
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
                        "Radius connection")
                .client(clientModel)
                .detail(RADIUS_HOST,
                        clientConnection.getRemoteAddr());
    }
}
