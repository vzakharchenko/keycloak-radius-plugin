package com.github.vzakharchenko.radius.client;

import org.keycloak.Config;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.*;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.protocol.LoginProtocolFactory;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.representations.idm.ClientRepresentation;

import java.util.HashMap;
import java.util.Map;

import static com.github.vzakharchenko.radius.mappers.RadiusPasswordMapper.OIDC_RADIUS_PASSWORD_ID;

public class RadiusLoginProtocolFactory implements LoginProtocolFactory {

    public static final String RADIUS_PROTOCOL = "radius-protocol";
    public static final String ONE_TIME_RADIUS_PASSWORD = "OneTime Radius Password";


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

    public static ProtocolMapperModel createClaimMapper() {
        ProtocolMapperModel mapper = new ProtocolMapperModel();
        mapper.setName(ONE_TIME_RADIUS_PASSWORD);
        mapper.setProtocolMapper(OIDC_RADIUS_PASSWORD_ID);
        mapper.setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        Map<String, String> config = new HashMap<String, String>();
        mapper.setConfig(config);
        return mapper;
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        ProviderFactory<LoginProtocol> protocolProviderFactory = factory
                .getProviderFactory(LoginProtocol.class, OIDCLoginProtocol.LOGIN_PROTOCOL);
        ((LoginProtocolFactory) protocolProviderFactory)
                .getBuiltinMappers().put(ONE_TIME_RADIUS_PASSWORD, createClaimMapper());
    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return RADIUS_PROTOCOL;
    }
}
