package com.github.vzakharchenko.radius.radius.provider;

import com.github.vzakharchenko.radius.radius.server.RadiusServerProviderFactory;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import java.util.List;

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


    }
}
