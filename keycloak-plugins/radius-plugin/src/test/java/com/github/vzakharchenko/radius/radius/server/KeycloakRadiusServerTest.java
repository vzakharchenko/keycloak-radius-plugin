package com.github.vzakharchenko.radius.radius.server;

import com.github.vzakharchenko.radius.models.RadiusServerSettings;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

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
