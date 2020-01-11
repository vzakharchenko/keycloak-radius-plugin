package ua.zaskarius.keycloak.plugins.radius.radius.provider;

import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.configuration.ConfigurationScheduledTask;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProviderFactory;
import ua.zaskarius.keycloak.plugins.radius.radius.server.RadiusServerProviderFactory;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class KeycloakRadiusServerProviderFactoryTest extends AbstractRadiusTest {
    private RadiusServerProviderFactory providerFactory = new RadiusServerProviderFactory();


    @Override
    protected List<? extends Object> resetMock() {
        return null;
    }

    @Test
    public void testMethods() {
        providerFactory.close();
        assertNotNull(providerFactory.create(session));
        assertEquals(providerFactory.getId(), RadiusServerProviderFactory.RADIUS_PROVIDER);
        providerFactory.init(null);
        providerFactory.close();
    }

    @Test
    public void testInit() {
        providerFactory.postInit(keycloakSessionFactory);
        ConfigurationScheduledTask instance = (ConfigurationScheduledTask)
                ConfigurationScheduledTask.getInstance();
        Map<Class<? extends IRadiusServerProviderFactory>,
                IRadiusServerProviderFactory<? extends IRadiusServerProvider>>
                connectionProviderMap = instance.connectionProviderMap;
        assertEquals(connectionProviderMap.size(), 1);
        IRadiusServerProviderFactory<? extends IRadiusServerProvider> providerFactory =
                connectionProviderMap.get(RadiusServerProviderFactory.class);
        assertEquals(providerFactory, this.providerFactory);


    }
}
