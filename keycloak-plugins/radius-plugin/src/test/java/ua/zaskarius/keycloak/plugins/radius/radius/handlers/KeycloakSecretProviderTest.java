package ua.zaskarius.keycloak.plugins.radius.radius.handlers;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import ua.zaskarius.keycloak.plugins.radius.test.ModelBuilder;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static ua.zaskarius.keycloak.plugins.radius.test.ModelBuilder.SHARED;

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
        assertEquals(sharedSecret, SHARED);
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
                .getSharedSecret(new InetSocketAddress("dfsf", 0));
        assertNull(sharedSecret);
    }


    @Override
    protected List<? extends Object> resetMock() {
        return Arrays.asList();
    }
}
