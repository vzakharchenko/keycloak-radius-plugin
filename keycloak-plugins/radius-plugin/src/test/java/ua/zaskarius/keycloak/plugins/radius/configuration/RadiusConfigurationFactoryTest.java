package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ua.zaskarius.keycloak.plugins.radius
        .configuration.RadiusConfigurationFactory.RADIUS_JPA;
import static ua.zaskarius.keycloak.plugins
        .radius.configuration.RadiusConfigurationFactory.RADIUS_JTA_FACTORY;

public class RadiusConfigurationFactoryTest extends AbstractRadiusTest {
    private RadiusConfigurationFactory radiusConfigurationFactory =
            new RadiusConfigurationFactory();

    @Test
    public void testMethods() {
        radiusConfigurationFactory.close();
        radiusConfigurationFactory.init(null);
        radiusConfigurationFactory.postInit(null);
        assertNotNull(radiusConfigurationFactory.create(session));
        assertEquals(radiusConfigurationFactory.getChangelogLocation(),
                "radius-changelog.xml");
        assertEquals(radiusConfigurationFactory.getFactoryId(), RADIUS_JTA_FACTORY);
        assertEquals(radiusConfigurationFactory.getId(), RADIUS_JPA);
        assertEquals(radiusConfigurationFactory.getEntities().size(), 2);
    }
}
