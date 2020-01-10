package ua.zaskarius.keycloak.plugins.radius.event;

import ua.zaskarius.keycloak.plugins.radius.RadiusHelper;
import ua.zaskarius.keycloak.plugins.radius.configuration.IRadiusConfiguration;
import ua.zaskarius.keycloak.plugins.radius.configuration.RadiusConfigHelper;
import ua.zaskarius.keycloak.plugins.radius.mappers.RadiusPasswordMapper;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusCommonSettings;
import ua.zaskarius.keycloak.plugins.radius.password.UpdateRadiusPassword;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ua.zaskarius.keycloak.plugins.radius.radius.provider.RadiusRadiusProvider.READ_MIKROTIK_PASSWORD;

public abstract class AbstractAuthentificatorAction implements AdminEventAction {

    private ClientModel getClientByIdOrName(RealmModel realm, String clientName) {
        ClientModel client = realm.getClientByClientId(clientName);
        if (client == null) {
            client = realm.getClientById(clientName);
        }
        return client;
    }

    protected String getProviderId() {
        return RadiusPasswordMapper.OIDC_RADIUS_PASSWORD_ID;
    }

    protected String getMapperName() {
        return "Radius Password Provider";
    }

    private void initProtocolMapperByType(ClientModel client) {
        ProtocolMapperModel mapper = client.getProtocolMappers()
                .stream().filter(protocolMapperModel -> protocolMapperModel
                        .getProtocolMapper().equalsIgnoreCase(
                                getProviderId()))
                .findFirst().orElse(null);
        if (mapper == null) {
            mapper = new ProtocolMapperModel();
            mapper.setProtocolMapper(getProviderId());
            mapper.setName(getMapperName());
            mapper.setProtocol(client.getProtocol());
            Map<String, String> configMap = new HashMap<>();
            configMap.put("id.token.claim", "true");
            configMap.put("access.token.claim", "true");
            configMap.put("userinfo.token.claim", "true");
            mapper.setConfig(configMap);
            client.addProtocolMapper(mapper);
        }
    }


    private void initRealm(RealmModel realm) {
        if (realm.getRequiredActionProviderByAlias(UpdateRadiusPassword
                .UPDATE_RADIUS_PASSWORD) == null) {
            RequiredActionProviderModel rPM = new RequiredActionProviderModel();
            rPM.setEnabled(true);
            rPM.setAlias(UpdateRadiusPassword
                    .UPDATE_RADIUS_PASSWORD);
            rPM.setName("Update Mikrotik Password");
            rPM.setProviderId(UpdateRadiusPassword
                    .UPDATE_RADIUS_PASSWORD);
            rPM.setDefaultAction(false);
            rPM.setPriority(50);
            realm.addRequiredActionProvider(rPM);
        }
        realm.setAdminEventsEnabled(true);
        realm.setEventsEnabled(true);
        RoleModel role = realm.getRole(READ_MIKROTIK_PASSWORD);
        if (role == null) {
            realm.addRole(READ_MIKROTIK_PASSWORD);
        }
    }

    protected abstract String getAuthConfigId(KeycloakSession session,
                                              AdminEvent event, RealmModel realm);

    @Override
    public void invokeAction(KeycloakSession session, AdminEvent event) {
        RealmModel realm = session.realms().getRealm(event.getRealmId());
        if (RadiusHelper.isUseRadius(realm)) {

            IRadiusConfiguration config = RadiusConfigHelper.getConfig();
            RadiusCommonSettings commonSettings = config.getCommonSettings(realm);
            if (
                    Objects.equals(commonSettings.getId(),
                            getAuthConfigId(session, event, realm)) &&
                            (event.getOperationType() == OperationType.CREATE
                                    ||
                                    event.getOperationType() == OperationType.UPDATE)
            ) {
                List<String> clients = commonSettings.getClients();
                if (!clients.isEmpty()) {
                    for (String clientId : clients) {
                        ClientModel client = getClientByIdOrName(realm, clientId);
                        if (client != null) {
                            initProtocolMapperByType(client);
                        }
                    }
                }
                initRealm(realm);
            }
        }
    }
}
