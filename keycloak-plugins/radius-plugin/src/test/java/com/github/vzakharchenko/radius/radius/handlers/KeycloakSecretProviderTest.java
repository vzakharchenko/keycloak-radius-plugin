package com.github.vzakharchenko.radius.radius.handlers;

import com.github.vzakharchenko.radius.models.RadiusServerSettings;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import com.github.vzakharchenko.radius.test.ModelBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class KeycloakSecretProviderTest extends AbstractRadiusTest {

    private KeycloakSecretProvider keycloakSecretProvider;
    private InetSocketAddress inetSocketAddress;

    @BeforeMethod
    public void beforeMethod() {
        keycloakSecretProvider = new KeycloakSecretProvider();
        inetSocketAddress = new InetSocketAddress(ModelBuilder.IP, 0);
    }

    @Test
    public void testIpSharedSecret() {
        String sharedSecret = keycloakSecretProvider.getSharedSecret(inetSocketAddress);
        assertEquals(sharedSecret, "ip_secret");
    }

    @Test
    public void testSharedSecretWrong() {
        String sharedSecret = keycloakSecretProvider
                .getSharedSecret(new InetSocketAddress("111.111.111.111", 0));
        Assert.assertEquals(sharedSecret, ModelBuilder.SHARED);
    }

    @Test
    public void testSharedSecretNull() {
        RadiusServerSettings radiusServerSettings = ModelBuilder.createRadiusServerSettings();
        radiusServerSettings.setSecret(null);
        when(configuration.getRadiusSettings()).thenReturn(radiusServerSettings);
        String sharedSecret = keycloakSecretProvider
                .getSharedSecret(new InetSocketAddress("111.111.111.111", 0));
        assertNull(sharedSecret);
    }

    @Test
    public void testSharedSecretNull2() {
        String sharedSecret = keycloakSecretProvider
                .getSharedSecret(new InetSocketAddress("not-existing-hostname.", 0));
        assertNull(sharedSecret);
    }


    @Override
    protected List<? extends Object> resetMock() {
        return Arrays.asList();
    }
}
