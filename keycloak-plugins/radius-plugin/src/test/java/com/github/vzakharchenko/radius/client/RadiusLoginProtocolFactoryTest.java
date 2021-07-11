package com.github.vzakharchenko.radius.client;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.protocol.LoginProtocolFactory;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class RadiusLoginProtocolFactoryTest extends AbstractRadiusTest {
    private final RadiusLoginProtocolFactory radiusLoginProtocolFactory = new RadiusLoginProtocolFactory();

    @Test
    public void testMethods() {
        radiusLoginProtocolFactory.setupClientDefaults(null, null);
        radiusLoginProtocolFactory.createDefaultClientScopes(null, false);
        radiusLoginProtocolFactory.close();
        radiusLoginProtocolFactory.init(null);

        assertNull(radiusLoginProtocolFactory.createProtocolEndpoint(null, null));
        assertNotNull(radiusLoginProtocolFactory.create(session));
        assertEquals(radiusLoginProtocolFactory.getBuiltinMappers().size(), 0);
        Assert.assertEquals(radiusLoginProtocolFactory.getId(), RadiusLoginProtocolFactory.RADIUS_PROTOCOL);


    }

    @Test
    public void testPostInit() {
        LoginProtocolFactory protocolFactory = mock(LoginProtocolFactory.class);
        when(keycloakSessionFactory.getProviderFactory(LoginProtocol.class, OIDCLoginProtocol.LOGIN_PROTOCOL))
                .thenReturn(protocolFactory);
        HashMap<String, ProtocolMapperModel> map = new HashMap<>();
        when(protocolFactory.getBuiltinMappers()).thenReturn(map);
        radiusLoginProtocolFactory.postInit(keycloakSessionFactory);
        assertEquals(map.size(), 2);

    }
}
