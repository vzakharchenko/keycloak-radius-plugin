package com.github.vzakharchenko.radius.client;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;

import static org.testng.Assert.*;

public class RadiusLoginProtocolFactoryTest extends AbstractRadiusTest {
    private RadiusLoginProtocolFactory radiusLoginProtocolFactory = new RadiusLoginProtocolFactory();

    @Test
    public void testMethods() {
        radiusLoginProtocolFactory.setupClientDefaults(null, null);
        radiusLoginProtocolFactory.createDefaultClientScopes(null, false);
        radiusLoginProtocolFactory.close();
        radiusLoginProtocolFactory.init(null);
        radiusLoginProtocolFactory.postInit(keycloakSessionFactory);
        assertNull(radiusLoginProtocolFactory.createProtocolEndpoint(null, null));
        assertNotNull(radiusLoginProtocolFactory.create(session));
        assertEquals(radiusLoginProtocolFactory.getBuiltinMappers().size(), 0);
        Assert.assertEquals(radiusLoginProtocolFactory.getId(), RadiusLoginProtocolFactory.RADIUS_PROTOCOL);


    }
}
