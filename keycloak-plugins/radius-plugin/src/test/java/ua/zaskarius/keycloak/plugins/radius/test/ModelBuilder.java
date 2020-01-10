package ua.zaskarius.keycloak.plugins.radius.test;

import ua.zaskarius.keycloak.plugins.radius.models.RadiusCommonSettings;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.password.RadiusCredentialModel;
import ua.zaskarius.keycloak.plugins.radius.radius.provider.RadiusRadiusProviderFactory;
import org.keycloak.credential.CredentialModel;

import java.util.Arrays;

public class ModelBuilder {

    public static final String SHARED = "shared";
    public static final String IP = "123.123.123.123";

    public static RadiusServerSettings createRadiusServerSettings() {
        RadiusServerSettings radiusServerSettings = new RadiusServerSettings();
        radiusServerSettings.setSecret(SHARED);
        radiusServerSettings.setUrl(Arrays.asList("127.0.0.1", IP));
        return radiusServerSettings;
    }

    public static CredentialModel createCredentialModel() {
        return createCredentialModel(123L);
    }

    public static CredentialModel createCredentialModel(Long createDate) {
        return createCredentialModel(createDate,
                "{\"password\":\"secret\"}");
    }


    public static CredentialModel createCredentialModel(Long createDate,
                                                              String secret) {
        CredentialModel credentialModel = new CredentialModel();
        credentialModel.setCreatedDate(createDate);
        credentialModel.setType(RadiusCredentialModel.TYPE);
        credentialModel.setId("id");
        credentialModel.setSecretData(secret);
        credentialModel.setCredentialData("{}");
        return credentialModel;
    }

    public static RadiusCommonSettings getRadiusCommonSettings() {
        RadiusCommonSettings radiusCommonSettings = new RadiusCommonSettings();
        radiusCommonSettings.setId("id");
        radiusCommonSettings.setExecutionId("execId");
        radiusCommonSettings.setProvider(RadiusRadiusProviderFactory
                .KEYCLOAK_RADIUS_SERVER);
        radiusCommonSettings.setUseRadius(true);
        return radiusCommonSettings;
    }
}
