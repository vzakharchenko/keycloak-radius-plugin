package ua.zaskarius.keycloak.plugins.radius.configuration;

import ua.zaskarius.keycloak.plugins.radius.models.RadiusCommonSettings;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.AuthenticatorConfigModel;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class FlowConfigurationTest extends AbstractRadiusTest {
    public static final String FLOW_ID = "flowId";
    public static final String CONFIG_ID = "CONFIG_ID";
    public static final String RADIUS_CONFIG_ID = "RADIUS_CONFIG_ID";
    private FlowRadiusConfiguration flowConfiguration = new FlowRadiusConfiguration();


    private AuthenticationFlowModel authenticationFlowModel;

    private AuthenticationExecutionModel commonExecutionModel;
    private AuthenticationExecutionModel radiusExecutionModel;

    private AuthenticatorConfigModel authenticatorRadiusConfigModel;
    private AuthenticatorConfigModel authenticatorCommonConfigModel;

    private Map<String, String> getCommonConfig() {
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put(RadiusCommonSettingFactory.RADIUS_PROVIDERS, "provider");
        stringStringMap.put(RadiusCommonSettingFactory.USE_RADIUS, "true");
        stringStringMap.put(RadiusCommonSettingFactory.RADIUS_CLIENTS, "111.111.111.111,222.222.222.222");
        return stringStringMap;
    }

    private Map<String, String> getRadiusConfig() {
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put(RadiusSettingFactory.RADIUS_SERVER_SECRET, "secret");
        stringStringMap.put(RadiusSettingFactory.RADIUS_SERVER_HOST, "111.111.111.111,222.222.222.222");
        return stringStringMap;
    }

    @BeforeMethod
    private void beforeTests() {
        authenticationFlowModel = new AuthenticationFlowModel();
        authenticatorCommonConfigModel = new AuthenticatorConfigModel();
        authenticatorCommonConfigModel.setId(CONFIG_ID);
        authenticatorCommonConfigModel.setConfig(
                getCommonConfig()
        );

        authenticatorRadiusConfigModel = new AuthenticatorConfigModel();
        authenticatorRadiusConfigModel.setId(CONFIG_ID);
        authenticatorRadiusConfigModel.setConfig(
                getRadiusConfig()
        );
        commonExecutionModel = new AuthenticationExecutionModel();
        radiusExecutionModel = new AuthenticationExecutionModel();
        authenticationFlowModel.setAlias(IRadiusConfiguration.RADIUS_SETTINGS);
        authenticationFlowModel.setId(FLOW_ID);
        radiusExecutionModel.setFlowId(FLOW_ID);
        commonExecutionModel.setFlowId(FLOW_ID);
        radiusExecutionModel.setAuthenticator(RadiusSettingFactory.RADIUS_SETTINGS);
        radiusExecutionModel.setAuthenticator(RadiusSettingFactory.RADIUS_SETTINGS);
        commonExecutionModel.setAuthenticator(RadiusCommonSettingFactory.RADIUS_PROVIDER_SETTINGS);
        radiusExecutionModel.setAuthenticatorConfig(RADIUS_CONFIG_ID);
        commonExecutionModel.setAuthenticatorConfig(CONFIG_ID);
        when(realmModel.getAuthenticationFlows()).thenReturn(
                Arrays.asList(authenticationFlowModel)
        );
        when(realmModel.getAuthenticationExecutions(FLOW_ID))
                .thenReturn(Arrays.asList(commonExecutionModel, radiusExecutionModel));
        when(realmModel.getAuthenticatorConfigById(CONFIG_ID))
                .thenReturn(authenticatorCommonConfigModel);
        when(realmModel.getAuthenticatorConfigById(RADIUS_CONFIG_ID))
                .thenReturn(authenticatorRadiusConfigModel);

        when(realmModel.addAuthenticationFlow(any())).thenReturn(authenticationFlowModel);
        when(realmModel.addAuthenticatorExecution(any())).thenReturn(commonExecutionModel);
    }

    @Test
    public void testGetFlow() {
        assertEquals(flowConfiguration
                .getFlow(realmModel,
                        IRadiusConfiguration.RADIUS_SETTINGS), authenticationFlowModel);
    }

    @Test
    public void testGetFlowNull() {
        assertNull(flowConfiguration
                .getFlow(realmModel,
                        "null flow"));
    }

    @Test
    public void testGetExecution() {

        assertEquals(flowConfiguration
                        .getExecution(realmModel,
                                FLOW_ID, RadiusCommonSettingFactory.RADIUS_PROVIDER_SETTINGS),
                commonExecutionModel);
    }

    @Test
    public void testGetExecutionNull() {

        assertNull(flowConfiguration
                .getExecution(realmModel,
                        FLOW_ID, "TEST"));
        commonExecutionModel.setAuthenticatorConfig(null);
        assertNull(flowConfiguration
                .getExecution(realmModel,
                        FLOW_ID, RadiusCommonSettingFactory.RADIUS_PROVIDER_SETTINGS));
    }

    @Test
    public void testGetConfig() {
        Map<String, String> config = flowConfiguration
                .getConfig(realmModel, IRadiusConfiguration.RADIUS_SETTINGS, RadiusCommonSettingFactory.RADIUS_PROVIDER_SETTINGS);
        assertEquals(config.get(RadiusCommonSettingFactory.RADIUS_PROVIDERS), "provider");
        assertEquals(config.get(RadiusCommonSettingFactory.USE_RADIUS), "true");
        assertEquals(config.get(RadiusCommonSettingFactory.RADIUS_CLIENTS), "111.111.111.111,222.222.222.222");
    }

    @Test
    public void testGetConfigFlowNull() {
        Map<String, String> config = flowConfiguration
                .getConfig(realmModel, "FlowNull", RadiusCommonSettingFactory.RADIUS_PROVIDER_SETTINGS);
        assertTrue(config.isEmpty());
    }

    @Test
    public void testGetConfigFlowExecutionNull() {
        Map<String, String> config = flowConfiguration
                .getConfig(realmModel, IRadiusConfiguration.RADIUS_SETTINGS, "Execution null");
        assertTrue(config.isEmpty());
    }


    @Test
    public void testGetConfigNull() {
        when(realmModel.getAuthenticatorConfigById(CONFIG_ID))
                .thenReturn(null);
        Map<String, String> config = flowConfiguration
                .getConfig(realmModel, IRadiusConfiguration.RADIUS_SETTINGS, RadiusCommonSettingFactory.RADIUS_PROVIDER_SETTINGS);
        assertTrue(config.isEmpty());
    }

    @Test
    public void testCommonConfig() {
        RadiusCommonSettings radiusCommonSettings = flowConfiguration
                .getCommonSettings(realmModel);
        assertNotNull(radiusCommonSettings);
        assertEquals(radiusCommonSettings.getProvider(), "provider");
        assertTrue(radiusCommonSettings.isUseRadius());
        assertEquals(radiusCommonSettings.getClients().size(), 2);
    }

    @Test
    public void testRadiusConfig() {
        RadiusServerSettings radiusSettings = flowConfiguration
                .getRadiusSettings(realmModel);
        assertNotNull(radiusSettings);
        assertEquals(radiusSettings.getUrl().size(), 2);
        assertEquals(radiusSettings.getSecret(), "secret");
    }

    @Test
    public void testisUsedRadius() {
        assertTrue(flowConfiguration.isUsedRadius(realmModel));
    }

    @Test
    public void testGetExecutions() {
        assertEquals(flowConfiguration.getExecutions().size(), 2);
    }

    @Test
    public void testInitDoNotAddAnythings() {
        flowConfiguration.init(realmModel);
        verify(realmModel, never()).addAuthenticationFlow(any());
        verify(realmModel, never()).addAuthenticatorExecution(any());
    }

    @Test
    public void testInit() {
        when(realmModel.getAuthenticationFlows()).thenReturn(
                Collections.emptyList()
        );
        when(realmModel.getAuthenticationExecutions(FLOW_ID))
                .thenReturn(Collections.singletonList(commonExecutionModel));
        flowConfiguration.init(realmModel);
        verify(realmModel, times(2)).addAuthenticationFlow(any());
        verify(realmModel, times(2)).addAuthenticatorExecution(any());
    }
}
