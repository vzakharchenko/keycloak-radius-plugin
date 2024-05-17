package com.github.vzakharchenko.radius.mappers;

import com.github.vzakharchenko.radius.RadiusHelper;
import org.apache.commons.lang3.BooleanUtils;
import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
import java.util.List;

public class RadiusPasswordMapper extends AbstractOIDCProtocolMapper implements
        OIDCAccessTokenMapper,
        OIDCIDTokenMapper,
        UserInfoTokenMapper {
    public static final String OIDC_RADIUS_PASSWORD_ID = "oidc-radius-password";
    public static final String PREFERRED_USERNAME = "preferred_username";
    public static final String PASSWORD_FIELD = "s";
    private static final List<ProviderConfigProperty> PROVIDER_CONFIG_PROPERTIES =
            new ArrayList<>();

    public static final String ONE_TIME_PASSWORD = "oneTimePassword";

    static {
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(PROVIDER_CONFIG_PROPERTIES,
                RadiusPasswordMapper.class);
        PROVIDER_CONFIG_PROPERTIES.add(new ProviderConfigProperty(ONE_TIME_PASSWORD,
                "One Time Password",
                "If enabled, the password is valid only once," +
                        " otherwise the password is active while the session is active",
                ProviderConfigProperty.BOOLEAN_TYPE, Boolean.TRUE));

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
        return "Send Session Password in token";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return PROVIDER_CONFIG_PROPERTIES;
    }

    @Override
    public String getId() {
        return OIDC_RADIUS_PASSWORD_ID;
    }

    //CHECKSTYLE:OFF
    @Override
    protected void setClaim(IDToken token,
                            ProtocolMapperModel mappingModel,
                            UserSessionModel userSession,
                            KeycloakSession keycloakSession,
                            ClientSessionContext clientSessionCtx) {
        //CHECKSTYLE:ON
        if (RadiusHelper.isUseRadius()) {
            token.getOtherClaims().put("s",
                    getPassword(userSession, token, BooleanUtils.toBooleanObject(
                            mappingModel.getConfig().get(ONE_TIME_PASSWORD))));
            token.getOtherClaims().put("n", userNameFieldMapper());
            token.getOtherClaims().put("np", passwordFieldMapper());
        }
    }

    protected String userNameFieldMapper() {
        return PREFERRED_USERNAME;
    }

    protected String passwordFieldMapper() {
        return PASSWORD_FIELD;
    }

    protected String getPassword(UserSessionModel userSession,
                                 IDToken token,
                                 Boolean oneTomePassword) {
        IRadiusSessionPasswordManager radiusSessionPasswordManager =
                RadiusSessionPasswordManager.getInstance();
        return radiusSessionPasswordManager
                .password(userSession, token, oneTomePassword);
    }

}
