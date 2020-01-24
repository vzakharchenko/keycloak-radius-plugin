package com.github.vzakharchenko.radius.client;

import org.keycloak.Config;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.*;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.protocol.LoginProtocolFactory;
import org.keycloak.representations.idm.ClientRepresentation;

import java.util.HashMap;
import java.util.Map;

public class RadiusLoginProtocolFactory implements LoginProtocolFactory {

    public static final String RADIUS_PROTOCOL = "radius-protocol";


    @Override
    public Map<String, ProtocolMapperModel> getBuiltinMappers() {
        return new HashMap<>();
    }

    @Override
    public Object createProtocolEndpoint(RealmModel realm, EventBuilder event) {
        return null;
    }

    @Override
    public void createDefaultClientScopes(RealmModel newRealm, boolean addScopesToExistingClients) {

    }

    @Override
    public void setupClientDefaults(ClientRepresentation rep, ClientModel newClient) {
    }

    @Override
    public LoginProtocol create(KeycloakSession session) {
        return new RadiusLoginProtocol();
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
        return RADIUS_PROTOCOL;
    }
}
