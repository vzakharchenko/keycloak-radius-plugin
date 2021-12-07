package com.github.vzakharchenko.radius.client;

import com.github.vzakharchenko.radius.mappers.RadiusPasswordMapper;
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
import static org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper.*;

public class RadiusLoginProtocolFactory implements LoginProtocolFactory {

    public static final String RADIUS_PROTOCOL = "radius-protocol";
    public static final String ONE_TIME_RADIUS_PASSWORD = "OneTime Radius Password";
    public static final String SESSION_RADIUS_PASSWORD = "Session Radius Password";
    public static final String OTP = "radius.OTP";
    public static final String TRUE = "true";


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
        newClient.setAttribute(OTP, TRUE);
    }

    @Override
    public LoginProtocol create(KeycloakSession session) {
        return new RadiusLoginProtocol();
    }

    @Override
    public void init(Config.Scope config) {

    }

    public static ProtocolMapperModel createClaimMapper(String id,
                                                        String name, boolean onetimePassword) {
        ProtocolMapperModel mapper = new ProtocolMapperModel();
        mapper.setName(name);
        mapper.setProtocolMapper(id);
        mapper.setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        Map<String, String> config = new HashMap<>();
        config.put(INCLUDE_IN_ACCESS_TOKEN, "true");
        config.put(INCLUDE_IN_ID_TOKEN, "true");
        config.put(INCLUDE_IN_USERINFO, "true");
        config.put(RadiusPasswordMapper.ONE_TIME_PASSWORD, String.valueOf(onetimePassword));
        mapper.setConfig(config);
        return mapper;
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        ProviderFactory<LoginProtocol> protocolProviderFactory = factory
                .getProviderFactory(LoginProtocol.class, OIDCLoginProtocol.LOGIN_PROTOCOL);
        ((LoginProtocolFactory) protocolProviderFactory)
                .getBuiltinMappers().put(ONE_TIME_RADIUS_PASSWORD,
                createClaimMapper(OIDC_RADIUS_PASSWORD_ID,
                        ONE_TIME_RADIUS_PASSWORD, true));
        ((LoginProtocolFactory) protocolProviderFactory)
                .getBuiltinMappers().put(SESSION_RADIUS_PASSWORD,
                createClaimMapper(OIDC_RADIUS_PASSWORD_ID,
                        SESSION_RADIUS_PASSWORD, false));
    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return RADIUS_PROTOCOL;
    }
}
