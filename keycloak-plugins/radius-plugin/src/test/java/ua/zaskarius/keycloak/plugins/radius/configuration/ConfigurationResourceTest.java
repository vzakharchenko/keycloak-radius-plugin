package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.keycloak.services.ForbiddenException;
import org.keycloak.services.managers.AuthenticationManager;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.models.ConfigurationRepresentation;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusConfigModel;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ConfigurationResourceTest extends AbstractRadiusTest {
    private ConfigurationResourceImpl configurationResource;

    @Mock
    private IRadiusConfigJPA radiusConfigJPA;

    private RadiusConfigModel radiusConfigModel;

    @BeforeMethod
    public void beforeMethod() {
        reset(radiusConfigJPA);
        radiusConfigModel = new RadiusConfigModel();
        radiusConfigModel.setId("1");
        when(radiusConfigJPA.getConfig()).thenReturn(radiusConfigModel);
        when(radiusConfigJPA.saveConfig(any(), any())).thenReturn(radiusConfigModel);
        configurationResource = new ConfigurationResourceImpl(session);
        configurationResource.setRadiusConfigJPA(radiusConfigJPA);
    }

    @Test
    public void testGetConfig() {
        ConfigurationRepresentation config = configurationResource.getConfig();
        assertNotNull(config);
        assertEquals(config.getId(), "1");
    }

    @Test
    public void testGetConfigNull() {
        when(radiusConfigJPA.getConfig()).thenReturn(null);
        ConfigurationRepresentation config = configurationResource.getConfig();
        assertNotNull(config);
        assertEquals(config.getAccountPort(), 0);
    }

    @Test(expectedExceptions = ForbiddenException.class)
    public void testFail1() {
        when(keycloakHelper.getAuthResult(session)).thenReturn(null);
        configurationResource = new ConfigurationResourceImpl(session);
    }

    @Test(expectedExceptions = ForbiddenException.class)
    public void testFail2() {
        when(keycloakHelper.getAuthResult(session)).thenReturn(new AuthenticationManager
                .AuthResult(null, null, null));
        configurationResource = new ConfigurationResourceImpl(session);
    }
}
