package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import static org.testng.Assert.assertNotNull;

public class ConfigurationProviderTest extends AbstractRadiusTest {
    private ConfigurationProvider configurationProvider;

    @BeforeMethod
    public void beforeMethod() {
        configurationProvider = new ConfigurationProvider(session);
    }

    @Test
    public void testMethods() {
        configurationProvider.close();
        assertNotNull(configurationProvider.getResource());
    }
}
