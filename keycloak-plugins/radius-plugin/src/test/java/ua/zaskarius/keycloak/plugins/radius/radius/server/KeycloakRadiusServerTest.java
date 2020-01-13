package ua.zaskarius.keycloak.plugins.radius.radius.server;

import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.util.List;

import static org.mockito.Mockito.when;

public class KeycloakRadiusServerTest extends AbstractRadiusTest {


    @Test
    public void testServer() {
        KeycloakRadiusServer keycloakRadiusServer = new KeycloakRadiusServer(session);
        keycloakRadiusServer.getServer().close();
        keycloakRadiusServer.close();
    }

    @Test
    public void testServerSkipStart() {
        RadiusServerSettings serverSettings = new RadiusServerSettings();
        serverSettings.setUseRadius(false);
        when(configuration.getRadiusSettings()).thenReturn(serverSettings);
        KeycloakRadiusServer keycloakRadiusServer = new KeycloakRadiusServer(session);
        keycloakRadiusServer.getServer().close();
        keycloakRadiusServer.close();
    }

    @Override
    protected List<? extends Object> resetMock() {
        return null;
    }
}
