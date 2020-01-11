package ua.zaskarius.keycloak.plugins.radius.client;

import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import static org.testng.Assert.assertEquals;

public class RadiusLoginProtocolTest extends AbstractRadiusTest {

    private RadiusLoginProtocol radiusLoginProtocol = new RadiusLoginProtocol();

    @Test
    public void testMethods() {
        radiusLoginProtocol.authenticated(null, null, null);
        radiusLoginProtocol.backchannelLogout(null, null);
        radiusLoginProtocol.sendError(null, null);
        radiusLoginProtocol.backchannelLogout(null, null);
        radiusLoginProtocol.frontchannelLogout(null, null);
        radiusLoginProtocol.requireReauthentication(null, null);
        radiusLoginProtocol.finishLogout(null);
        radiusLoginProtocol.close();
        assertEquals(radiusLoginProtocol.setSession(session), radiusLoginProtocol);
        assertEquals(radiusLoginProtocol.setRealm(realmModel), radiusLoginProtocol);
        assertEquals(radiusLoginProtocol.setHttpHeaders(null), radiusLoginProtocol);
        assertEquals(radiusLoginProtocol.setEventBuilder(eventBuilder), radiusLoginProtocol);
        assertEquals(radiusLoginProtocol.setUriInfo(null), radiusLoginProtocol);
    }
}
