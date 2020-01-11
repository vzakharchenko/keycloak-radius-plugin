package ua.zaskarius.keycloak.plugins.radius;

import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import ua.zaskarius.keycloak.plugins.radius.configuration.IRadiusConfiguration;
import ua.zaskarius.keycloak.plugins.radius.configuration.RadiusConfigHelper;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.password.RadiusCredentialModel;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProvider;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

public final class RadiusHelper {

    private RadiusHelper() {
    }

    public static String generatePassword() {
        return new SecureRandom()
                .ints(10, 33, 122)
                .mapToObj(i -> String.valueOf((char) i))
                .collect(Collectors.joining()).replaceAll("\"", "@");
    }

    public static String getCurrentPassword(
            KeycloakSession keycloakSession,
            RealmModel realm,
            UserModel userModel
    ) {
        List<CredentialModel> credentials = keycloakSession
                .userCredentialManager()
                .getStoredCredentialsByType(realm, userModel, RadiusCredentialModel.TYPE);
        if (!credentials.isEmpty()) {
            CredentialModel credentialModel = credentials.get(0);
            RadiusCredentialModel model = RadiusCredentialModel
                    .createFromCredentialModel(credentialModel);
            return model.getSecret().getPassword();
        }
        return null;
    }

    public static String getPassword(
            KeycloakSession keycloakSession,
            RealmModel realm,
            UserModel userModel
    ) {
        String currentPassword = getCurrentPassword(keycloakSession, realm, userModel);
        if (currentPassword == null) {
            throw new IllegalStateException(userModel.getUsername() +
                    " does not have radius password");
        }
        return currentPassword;
    }

    public static IRadiusServerProvider getProvider(KeycloakSession session) {
        IRadiusConfiguration config = RadiusConfigHelper
                .getConfig();
        RadiusServerSettings radiusSettings = config
                .getRadiusSettings(session);
        return session
                .getProvider(IRadiusServerProvider.class,
                        radiusSettings.getProvider());
    }

    public static boolean isUseRadius(KeycloakSession session) {
        IRadiusConfiguration config = RadiusConfigHelper.getConfig();
        return config.getRadiusSettings(session).isUseRadius();
    }

}
