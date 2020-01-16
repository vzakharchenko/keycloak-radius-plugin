package ua.zaskarius.keycloak.plugins.radius.radius.server;

import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertNull;

public class KeycloakRadiusServerTest extends AbstractRadiusTest {


    @Test
    public void testServer() {
        KeycloakRadiusServer keycloakRadiusServer = new KeycloakRadiusServer(session);
        keycloakRadiusServer.init(realmModel);
        keycloakRadiusServer.getServer().close();
        keycloakRadiusServer.close();
    }

    @Test
    public void testServerSkipStart() {
        RadiusServerSettings serverSettings = new RadiusServerSettings();
        serverSettings.setUseUdpRadius(false);
        when(configuration.getRadiusSettings()).thenReturn(serverSettings);
        KeycloakRadiusServer keycloakRadiusServer = new KeycloakRadiusServer(session);
        assertNull(keycloakRadiusServer.getServer());
    }

    @Override
    protected List<? extends Object> resetMock() {
        return null;
    }
}
