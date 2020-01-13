package ua.zaskarius.keycloak.plugins.radius.mappers;

import ua.zaskarius.keycloak.plugins.radius.configuration.RadiusConfigHelper;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProvider;

import java.util.ArrayList;
import java.util.List;

public class RadiusPasswordMapper extends AbstractOIDCProtocolMapper implements
        OIDCAccessTokenMapper,
        OIDCIDTokenMapper,
        UserInfoTokenMapper {
    public static final String OIDC_RADIUS_PASSWORD_ID = "oidc-radius-password";
    public static final String RADIUS_SESSION_PASSWORD = "RADIUS_SESSION_PASSWORD";

    private static final List<ProviderConfigProperty> PROVIDER_CONFIG_PROPERTIES =
            new ArrayList<>();

    static {
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(PROVIDER_CONFIG_PROPERTIES,
                RadiusPasswordMapper.class);

    }

    @Override
    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
    public String getDisplayType() {
        return "Radius Session Password Mapper";
    }

    @Override
    public String getHelpText() {
        return "Send  Session Password in token";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return PROVIDER_CONFIG_PROPERTIES;
    }

    @Override
    public String getId() {
        return OIDC_RADIUS_PASSWORD_ID;
    }

    @Override
    protected void setClaim(IDToken token,
                            ProtocolMapperModel mappingModel,
                            UserSessionModel userSession,
                            KeycloakSession keycloakSession,
                            ClientSessionContext clientSessionCtx) {

        RadiusServerSettings commonSettings = RadiusConfigHelper.getConfig()
                .getRadiusSettings();
        if (commonSettings.isUseRadius()) {
            IRadiusServerProvider provider = keycloakSession
                    .getProvider(IRadiusServerProvider.class,
                            commonSettings.getProvider());
            RadiusSessionPasswordManager radiusSessionPasswordManager =
                    RadiusSessionPasswordManager.getInstance();
            String sessionNote = radiusSessionPasswordManager.password(userSession);

            userSession.setNote(RADIUS_SESSION_PASSWORD, sessionNote);
            token.getOtherClaims().put("s", sessionNote);
            token.getOtherClaims().put("n", provider.fieldName());
            token.getOtherClaims().put("np", provider.fieldPassword());
        }
    }

}
