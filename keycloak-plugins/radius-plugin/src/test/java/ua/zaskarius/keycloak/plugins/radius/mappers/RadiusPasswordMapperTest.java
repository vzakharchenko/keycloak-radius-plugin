package ua.zaskarius.keycloak.plugins.radius.mappers;

import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.representations.IDToken;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProvider;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import ua.zaskarius.keycloak.plugins.radius.test.ModelBuilder;

import static org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper.TOKEN_MAPPER_CATEGORY;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class RadiusPasswordMapperTest extends AbstractRadiusTest {
    private RadiusPasswordMapper passwordMapper = new RadiusPasswordMapper();

    private IDToken create() {
        IDToken idToken = new IDToken();
        return idToken;
    }

    @Test
    public void testMethods() {
        assertEquals(passwordMapper.getConfigProperties().size(), 3);
        assertEquals(passwordMapper.getDisplayCategory(), TOKEN_MAPPER_CATEGORY);
        assertEquals(passwordMapper.getProtocol(), OIDCLoginProtocol.LOGIN_PROTOCOL);
        assertEquals(passwordMapper.getDisplayType(), "Radius Session Password Mapper");
        assertEquals(passwordMapper.getHelpText(), "Send  Session Password in token");
        assertEquals(passwordMapper.getId(), RadiusPasswordMapper.OIDC_RADIUS_PASSWORD_ID);
    }

    @BeforeMethod
    public void beforeMethods() {
        IRadiusServerProvider provider = session
                .getProvider(IRadiusServerProvider.class);
        when(provider.fieldName()).thenReturn("n");
        when(provider.fieldPassword()).thenReturn("np");
    }

    @Test
    public void testSetClaim() {
        IDToken token = create();
        passwordMapper.setClaim(token, null, userSessionModel, session, null);
        assertEquals(token.getOtherClaims().get("s"), "123");
        assertEquals(token.getOtherClaims().get("n"), "n");
        assertEquals(token.getOtherClaims().get("np"), "np");
    }

    @Test
    public void testSetClaimEmpty() {

        when(userSessionModel.getNote(RadiusPasswordMapper.RADIUS_SESSION_PASSWORD))
                .thenReturn(null);
        IDToken token = create();
        passwordMapper.setClaim(token, null, userSessionModel, session, null);
        assertNotEquals(token.getOtherClaims().get("s"), "123");
        assertEquals(token.getOtherClaims().get("n"), "n");
        assertEquals(token.getOtherClaims().get("np"), "np");
    }

    @Test
    public void testWithoutRadius() {
        RadiusServerSettings radiusCommonSettings = ModelBuilder.createRadiusServerSettings();
        radiusCommonSettings.setUseRadius(false);
        when(configuration.getRadiusSettings())
                .thenReturn(radiusCommonSettings);
        IDToken token = create();
        passwordMapper.setClaim(token, null, userSessionModel, session, null);
        assertNull(token.getOtherClaims().get("s"));
        assertNull(token.getOtherClaims().get("n"));
        assertNull(token.getOtherClaims().get("np"));

    }
}
