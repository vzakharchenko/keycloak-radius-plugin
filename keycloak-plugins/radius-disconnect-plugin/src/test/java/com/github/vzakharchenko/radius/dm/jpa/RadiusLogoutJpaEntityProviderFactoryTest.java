package com.github.vzakharchenko.radius.dm.jpa;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import static com.github.vzakharchenko.radius.dm.jpa.RadiusLogoutJpaEntityProviderFactory.RADIUS_DISCONNECT_MESSAGE_FACTORY;
import static org.testng.Assert.assertEquals;

public class RadiusLogoutJpaEntityProviderFactoryTest extends AbstractRadiusTest {
    private RadiusLogoutJpaEntityProviderFactory radiusLogoutJpaEntityProviderFactory =
            new RadiusLogoutJpaEntityProviderFactory();

    @Test
    public void testMethods() {
        assertEquals(radiusLogoutJpaEntityProviderFactory.getEntities().size(),
                3);
        assertEquals(radiusLogoutJpaEntityProviderFactory.getChangelogLocation(),
                "dm-changelog.xml");
        assertEquals(radiusLogoutJpaEntityProviderFactory.getFactoryId(), "radius-dm");
        assertEquals(radiusLogoutJpaEntityProviderFactory.create(session),
                radiusLogoutJpaEntityProviderFactory);
        assertEquals(radiusLogoutJpaEntityProviderFactory.getId(),
                RADIUS_DISCONNECT_MESSAGE_FACTORY);
        radiusLogoutJpaEntityProviderFactory.close();
        radiusLogoutJpaEntityProviderFactory.init(null);
        radiusLogoutJpaEntityProviderFactory.postInit(null);
    }
}
