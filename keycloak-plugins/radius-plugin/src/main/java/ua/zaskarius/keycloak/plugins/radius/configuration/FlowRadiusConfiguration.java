package ua.zaskarius.keycloak.plugins.radius.configuration;


import org.keycloak.models.*;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusCommonSettings;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.radius.provider.RadiusRadiusProviderFactory;

import java.util.*;

public class FlowRadiusConfiguration implements IRadiusConfiguration {

    public static final String CONFIG_ID = "CONFIG_ID";
    public static final String EXEC_ID = "EXEC_ID";
    public static final String CONFIG_ALIAS = "CONFIG_ALIAS";

    protected FlowRadiusConfiguration() {
    }

    protected AuthenticationFlowModel getFlow(RealmModel realmModel, String flowAlias) {
        return realmModel.getAuthenticationFlows().stream().filter(
                authenticationFlowModel ->
                        Objects.equals(
                                authenticationFlowModel
                                        .getAlias().toLowerCase(Locale.US),
                                flowAlias.toLowerCase(Locale.US)))
                .findFirst().orElse(null);
    }

    protected AuthenticationExecutionModel getExecution(RealmModel realmModel,
                                                        String flowId, String name) {
        return realmModel
                .getAuthenticationExecutions(flowId)
                .stream().filter(
                        authenticationExecutionModel ->
                                Objects.equals(authenticationExecutionModel.getAuthenticator(),
                                        name)
                                        && authenticationExecutionModel
                                        .getAuthenticatorConfig() != null)
                .findFirst().orElse(null);
    }

    protected Map<String, String> getConfig(RealmModel realmModel,
                                            String flowalias, String name) {
        Map<String, String> config = new LinkedHashMap<>();
        AuthenticationFlowModel flow = getFlow(realmModel, flowalias);
        if (flow != null) {
            AuthenticationExecutionModel execution = getExecution(realmModel,
                    flow.getId(), name);
            if (execution != null) {
                AuthenticatorConfigModel authenticatorConfigModel = realmModel
                        .getAuthenticatorConfigById(execution.getAuthenticatorConfig());
                if (authenticatorConfigModel != null) {
                    config.putAll(authenticatorConfigModel.getConfig());
                    config.put(CONFIG_ID, authenticatorConfigModel.getId());
                    config.put(CONFIG_ALIAS, authenticatorConfigModel.getAlias());
                    config.put(EXEC_ID, execution.getId());
                }
            }
        }
        return config;
    }

    @Override
    public RadiusServerSettings getRadiusSettings(KeycloakSession session) {
       return null;
    }

    @Override
    public RadiusCommonSettings getCommonSettings(RealmModel realm) {
        Map<String, String> config = getConfig(realm,
                RADIUS_SETTINGS, RadiusCommonSettingFactory.RADIUS_PROVIDER_SETTINGS);

        RadiusCommonSettings mikrotikSetting = new RadiusCommonSettings();
        String provider = config.get(RadiusCommonSettingFactory.RADIUS_PROVIDERS);
        if (provider == null) {
            provider = RadiusRadiusProviderFactory.KEYCLOAK_RADIUS_SERVER;
        }
        mikrotikSetting.setProvider(provider);
        mikrotikSetting.setUseRadius(Boolean.parseBoolean(config
                .get(RadiusCommonSettingFactory.USE_RADIUS)));
        String clientListString = config.get(RadiusCommonSettingFactory.RADIUS_CLIENTS);
        List<String> clients;
        if (clientListString != null) {
            clients = Arrays.asList(clientListString.split(","));
        } else {
            clients = Collections.EMPTY_LIST;
        }
        mikrotikSetting.setClients(clients);
        mikrotikSetting.setId(config.get(CONFIG_ID));
        mikrotikSetting.setExecutionId(config.get(EXEC_ID));
        return mikrotikSetting;
    }

    @Override
    public RadiusServerSettings getRadiusSettings(RealmModel realm) {
        Map<String, String> config = getConfig(realm,
                RADIUS_SETTINGS, RadiusSettingFactory.RADIUS_SETTINGS);
        RadiusServerSettings radiusServerSettings = new RadiusServerSettings();
        radiusServerSettings.setSecret(config.get(RadiusSettingFactory.RADIUS_SERVER_SECRET));
        radiusServerSettings.setUrl(Arrays.asList(Objects.toString(config
                .get(RadiusSettingFactory.RADIUS_SERVER_HOST), "")
                .split(",")));
        return radiusServerSettings;
    }

    @Override
    public boolean isUsedRadius(RealmModel realmModel) {
        RadiusCommonSettings commonSettings = getCommonSettings(realmModel);
        return commonSettings.isUseRadius();
    }

    @Override
    public boolean isUsedRadius(KeycloakSession session) {
        return true;
    }

    public List<String> getExecutions() {
        return Arrays.asList(RadiusCommonSettingFactory.RADIUS_PROVIDER_SETTINGS,
                RadiusSettingFactory.RADIUS_SETTINGS);
    }

    @Override
    public boolean init(RealmModel realmModel) {
        boolean changed = false;
        AuthenticationFlowModel flow = getFlow(realmModel, RADIUS_SETTINGS);
        if (flow == null) {
            changed = true;
            AuthenticationFlowModel authenticationFlowModel = new AuthenticationFlowModel();
            authenticationFlowModel.setAlias("Radius Configuration");
            authenticationFlowModel.setProviderId("basic-flow");
            authenticationFlowModel.setTopLevel(true);
            authenticationFlowModel.setBuiltIn(true);
            authenticationFlowModel = realmModel
                    .addAuthenticationFlow(authenticationFlowModel);

            flow = new AuthenticationFlowModel();
            flow.setAlias(RADIUS_SETTINGS);
            flow.setDescription("Radius Server Configuration");
            flow.setProviderId("form-flow");
            flow.setTopLevel(false);
            flow.setBuiltIn(true);
            flow = realmModel.addAuthenticationFlow(flow);

            AuthenticationExecutionModel execution;

            execution = new AuthenticationExecutionModel();
            execution.setParentFlow(authenticationFlowModel.getId());
            execution.setRequirement(AuthenticationExecutionModel.Requirement.REQUIRED);
            execution.setAuthenticator("registration-page-form");
            execution.setPriority(10);
            execution.setAuthenticatorFlow(true);
            execution.setFlowId(flow.getId());
            realmModel.addAuthenticatorExecution(execution);
        }
        List<String> executions = getExecutions();
        for (String execution : executions) {
            AuthenticationExecutionModel executionModel = realmModel
                    .getAuthenticationExecutions(flow.getId())
                    .stream().filter(
                            authenticationExecutionModel ->
                                    Objects.equals(authenticationExecutionModel.getAuthenticator(),
                                            execution)).findFirst().orElse(null);
            if (executionModel == null) {
                changed = true;
                executionModel = new AuthenticationExecutionModel();
                executionModel.setParentFlow(flow.getId());
                executionModel.setAuthenticator(execution);
                executionModel.setAuthenticatorFlow(false);
                executionModel.setRequirement(AuthenticationExecutionModel.Requirement.REQUIRED);
                realmModel.addAuthenticatorExecution(executionModel);
            }
        }
        return changed;
    }
}
