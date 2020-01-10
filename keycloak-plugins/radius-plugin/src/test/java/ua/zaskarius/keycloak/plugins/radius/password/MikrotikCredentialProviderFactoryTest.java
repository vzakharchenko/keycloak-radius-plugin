package ua.zaskarius.keycloak.plugins.radius.password;

import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class MikrotikCredentialProviderFactoryTest extends AbstractRadiusTest {

    private RadiusCredentialProviderFactory
            radiusCredentialProviderFactory = new RadiusCredentialProviderFactory();

    @Test
    public void testMethods() {
        assertEquals(radiusCredentialProviderFactory
                        .getId(),
                RadiusCredentialProviderFactory.RADIUS_PROVIDER_ID);
        assertEquals(radiusCredentialProviderFactory
                        .create(session).getClass().getCanonicalName(),
                RadiusCredentialProvider.class.getCanonicalName());
    }


}
