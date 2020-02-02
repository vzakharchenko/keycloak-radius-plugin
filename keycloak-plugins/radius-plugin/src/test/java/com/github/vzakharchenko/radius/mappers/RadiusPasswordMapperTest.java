package com.github.vzakharchenko.radius.mappers;

import com.github.vzakharchenko.radius.models.RadiusServerSettings;
import com.github.vzakharchenko.radius.providers.IRadiusServerProvider;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import com.github.vzakharchenko.radius.test.ModelBuilder;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.representations.IDToken;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.vzakharchenko.radius.mappers.RadiusSessionPasswordManager.RADIUS_SESSION_PASSWORD;
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
        assertEquals(passwordMapper.getHelpText(), "Send Session Password in token");
        assertEquals(passwordMapper.getId(), RadiusPasswordMapper.OIDC_RADIUS_PASSWORD_ID);
    }

    @BeforeMethod
    public void beforeMethods() {
        IRadiusServerProvider provider = session
                .getProvider(IRadiusServerProvider.class);
    }

    @Test
    public void testSetClaim() {
        IDToken token = create();
        passwordMapper.setClaim(token, null, userSessionModel, session, null);
        assertEquals(token.getOtherClaims().get("s"), "123");
        assertEquals(token.getOtherClaims().get("n"), "preferred_username");
        assertEquals(token.getOtherClaims().get("np"), "s");
    }

    @Test
    public void testSetClaimEmpty() {

        when(userSessionModel.getNote(RADIUS_SESSION_PASSWORD))
                .thenReturn(null);
        IDToken token = create();
        passwordMapper.setClaim(token, null, userSessionModel, session, null);
        assertNotEquals(token.getOtherClaims().get("s"), "123");
        assertEquals(token.getOtherClaims().get("n"), "preferred_username");
        assertEquals(token.getOtherClaims().get("np"), "s");
    }

    @Test
    public void testWithoutRadius() {
        RadiusServerSettings radiusCommonSettings = ModelBuilder.createRadiusServerSettings();
        radiusCommonSettings.setUseUdpRadius(false);
        when(configuration.getRadiusSettings())
                .thenReturn(radiusCommonSettings);
        IDToken token = create();
        passwordMapper.setClaim(token, null, userSessionModel, session, null);
        assertNull(token.getOtherClaims().get("s"));
        assertNull(token.getOtherClaims().get("n"));
        assertNull(token.getOtherClaims().get("np"));

    }
}
