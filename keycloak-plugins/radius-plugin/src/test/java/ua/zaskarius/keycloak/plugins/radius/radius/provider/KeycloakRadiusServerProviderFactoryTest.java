package ua.zaskarius.keycloak.plugins.radius.radius.provider;

import ua.zaskarius.keycloak.plugins.radius.configuration.ConfigurationScheduledTask;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusConnectionProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusProviderFactory;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class KeycloakRadiusServerProviderFactoryTest extends AbstractRadiusTest {
    private RadiusRadiusProviderFactory providerFactory = new RadiusRadiusProviderFactory();


    @Override
    protected List<? extends Object> resetMock() {
        return null;
    }

    @Test
    public void testMethods() {
        providerFactory.close();
        assertNotNull(providerFactory.create(session));
        assertEquals(providerFactory.getId(), RadiusRadiusProviderFactory.KEYCLOAK_RADIUS_SERVER);
        providerFactory.init(null);
        providerFactory.close();
    }

    @Test
    public void testInit() {
        providerFactory.postInit(keycloakSessionFactory);
        ConfigurationScheduledTask instance = (ConfigurationScheduledTask)
                ConfigurationScheduledTask.getInstance();
        Map<Class<? extends IRadiusProviderFactory>,
                IRadiusProviderFactory<? extends IRadiusConnectionProvider>>
                connectionProviderMap = instance.connectionProviderMap;
        assertEquals(connectionProviderMap.size(), 1);
        IRadiusProviderFactory<? extends IRadiusConnectionProvider> providerFactory =
                connectionProviderMap.get(RadiusRadiusProviderFactory.class);
        assertEquals(providerFactory, this.providerFactory);
        assertEquals(instance.flowConfigurations.size(), 1);



    }
}
