package ua.zaskarius.keycloak.plugins.radius.configuration;

import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class RadiusConfigHelperTest extends AbstractRadiusTest {
    @Test
    public void testConfig(){
        assertNotNull(RadiusConfigHelper.getConfig());
    }
}
