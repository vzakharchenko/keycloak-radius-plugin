package ua.zaskarius.keycloak.plugins.radius.radius.server;

import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.when;

public class KeycloakRadiusServerTest extends AbstractRadiusTest {



    @Test
    public void testServer() {
        KeycloakRadiusServer keycloakRadiusServer = new KeycloakRadiusServer(session);
        keycloakRadiusServer.getServer().stop();
        keycloakRadiusServer.close();
    }

    @Test
    public void testServerSkipStart() {
        RadiusServerSettings serverSettings = new RadiusServerSettings();
        serverSettings.setUseRadius(false);
        when(configuration.getRadiusSettings(session)).thenReturn(serverSettings);
        KeycloakRadiusServer keycloakRadiusServer = new KeycloakRadiusServer(session);
        keycloakRadiusServer.getServer().stop();
        keycloakRadiusServer.close();
    }

    @Override
    protected List<? extends Object> resetMock() {
        return null;
    }
}
