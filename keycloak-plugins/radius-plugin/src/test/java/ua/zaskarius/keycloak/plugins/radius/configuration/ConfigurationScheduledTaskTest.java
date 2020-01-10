package ua.zaskarius.keycloak.plugins.radius.configuration;

import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusProviderFactory;
import ua.zaskarius.keycloak.plugins.radius.radius.provider.RadiusRadiusProvider;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class ConfigurationScheduledTaskTest extends AbstractRadiusTest {
    private ConfigurationScheduledTask
            configurationScheduledTask =
            (ConfigurationScheduledTask) ConfigurationScheduledTask.getInstance();
    @Mock
    private IRadiusProviderFactory connectionProviderFactory;
    @Mock
    private RadiusRadiusProvider connectionProvider;

    @BeforeMethod
    public void beforeMethod() {
        when(connectionProviderFactory.create(session)).thenReturn(connectionProvider);
    }

    @Test
    public void testRun() {
        ConfigurationScheduledTask.addConfiguration(configuration);
        ConfigurationScheduledTask.addConnectionProviderMap(connectionProviderFactory);
        configurationScheduledTask.run(session);
        verify(configuration).init(realmModel);
        verify(connectionProvider).init(realmModel);

    }

    @Test
    public void testRunWithoutConfigurations() {
        configurationScheduledTask.run(session);
        verify(configuration, never()).init(realmModel);
        verify(connectionProvider, never()).init(realmModel);
    }

    @Override
    protected List<? extends Object> resetMock() {
        return Arrays.asList(connectionProvider, connectionProviderFactory);
    }
}
