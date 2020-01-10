package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ua.zaskarius.keycloak.plugins.radius.configuration.ConfigurationEndpointFactory.RADIUS_CONFIGURATION_ENDPOINT;

public class ConfigurationEndpointFactoryTest extends AbstractRadiusTest {
    private ConfigurationEndpointFactory configurationEndpointFactory =
            new ConfigurationEndpointFactory();

    @Test
    public void methodTests() {
        configurationEndpointFactory.close();
        configurationEndpointFactory.init(null);
        configurationEndpointFactory.postInit(null);
        assertNotNull(configurationEndpointFactory.create(session));
        assertEquals(configurationEndpointFactory.getId(), RADIUS_CONFIGURATION_ENDPOINT);
    }
}
