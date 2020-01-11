package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.keycloak.models.RequiredActionProviderModel;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProviderFactory;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class ConfigurationScheduledTaskTest extends AbstractRadiusTest {
    private ConfigurationScheduledTask
            configurationScheduledTask =
            (ConfigurationScheduledTask) ConfigurationScheduledTask.getInstance();
    @Mock
    private IRadiusServerProviderFactory connectionProviderFactory;
    @Mock
    private IRadiusServerProvider connectionProvider;

    @BeforeMethod
    public void beforeMethod() {
        when(connectionProviderFactory.create(session)).thenReturn(connectionProvider);
    }

    @Test
    public void testRun() {
        when(connectionProvider.init(realmModel)).thenReturn(true);
        ConfigurationScheduledTask.addConnectionProviderMap(connectionProviderFactory);
        configurationScheduledTask.run(session);
        verify(connectionProvider).init(realmModel);

    }

    @Test
    public void testRunNotChange() {
        ConfigurationScheduledTask.addConnectionProviderMap(connectionProviderFactory);
        configurationScheduledTask.run(session);
        verify(connectionProvider).init(realmModel);

    }

    @Test(expectedExceptions = Exception.class)
    public void testRunException() {
        when(connectionProvider.init(realmModel)).thenThrow(new Exception());
        ConfigurationScheduledTask.addConnectionProviderMap(connectionProviderFactory);
        configurationScheduledTask.run(session);
        verify(connectionProvider).init(realmModel);

    }

    @Test
    public void testRunWithoutConfigurations() {
        configurationScheduledTask.run(session);
        verify(connectionProvider, never()).init(realmModel);
    }

    @Override
    protected List<? extends Object> resetMock() {
        return Arrays.asList(connectionProvider, connectionProviderFactory);
    }
}
