package ua.zaskarius.keycloak.plugins.radius.radius.server;

import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProvider;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.keycloak.services.scheduled.ClusterAwareScheduledTaskRunner;
import org.keycloak.timer.TimerProvider;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

public class RadiusServerProviderFactoryTest extends AbstractRadiusTest {
    private RadiusServerProviderFactory radiusServerProviderFactory = new RadiusServerProviderFactory();

    @Mock
    private IRadiusServerProvider mikrotikRadiusServer;

    @BeforeMethod
    public void beforeMethod() {
        radiusServerProviderFactory.setMikrotikRadiusServer(mikrotikRadiusServer);
    }

    @Test
    public void testMethods() {
        radiusServerProviderFactory.close();
        radiusServerProviderFactory.create(session);
        radiusServerProviderFactory.init(null);
        assertEquals(radiusServerProviderFactory.getId(), "radius-provider");
    }


    @Test
    public void testPostInit() {
        radiusServerProviderFactory.postInit(keycloakSessionFactory);
        verify(keycloakTransactionManager).begin();
        verify(keycloakTransactionManager).commit();
        TimerProvider provider = getProvider(TimerProvider.class);
        verify(provider).schedule(any(ClusterAwareScheduledTaskRunner.class), eq(60000L), anyString());

    }


    @Override
    protected List<? extends Object> resetMock() {
        return Arrays.asList(mikrotikRadiusServer);
    }
}
