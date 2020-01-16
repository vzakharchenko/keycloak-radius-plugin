package ua.zaskarius.keycloak.plugins.radius.radius.provider;

import org.keycloak.events.EventListenerProvider;
import org.keycloak.models.RequiredActionProviderModel;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.radius.server.KeycloakRadiusServer;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class MikrotikRadiusProviderTest extends AbstractRadiusTest {

    private KeycloakRadiusServer provider;
    @Mock
    private EventListenerProvider eventListenerProvider;

    @BeforeMethod
    public void beforeMethod() {
        provider = new KeycloakRadiusServer(session);
        when(realmModel.getRequiredActionProviderByAlias(any()))
                .thenReturn(new RequiredActionProviderModel());
    }

    @Override
    protected List<? extends Object> resetMock() {
        return Arrays.asList(eventListenerProvider);
    }

    @Test
    public void testMethods() {
        provider.close();
    }

    @Test
    public void testInitFalse() {
        assertFalse(provider.init(realmModel));
    }

    @Test
    public void testInitTrue() {
        when(realmModel.getRequiredActionProviderByAlias(any()))
                .thenReturn(null);
        assertTrue(provider.init(realmModel));
    }
}
