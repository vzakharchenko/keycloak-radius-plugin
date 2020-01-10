/*
 *
 *  * Copyright (c) 2017 ModelN, Inc. and/or its affiliates
 *  * and other contributors as indicated by the @author tags.
 *  *
 *
 */

package ua.zaskarius.keycloak.plugins.radius.event;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class RadiusEventListenerProviderFactory
        implements EventListenerProviderFactory,
        ServerInfoAwareProviderFactory {

    public static final String RADIUS_EVENT_LISTENER = "radius-event-listener";

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new RadiusEventProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return RADIUS_EVENT_LISTENER;
    }

    @Override
    public Map<String, String> getOperationalInfo() {
        return new LinkedHashMap<>();
    }
}
