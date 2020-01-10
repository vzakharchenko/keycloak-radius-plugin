package ua.zaskarius.keycloak.plugins.radius;

import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import ua.zaskarius.keycloak.plugins.radius.configuration.IRadiusConfiguration;
import ua.zaskarius.keycloak.plugins.radius.configuration.RadiusConfigHelper;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusCommonSettings;
import ua.zaskarius.keycloak.plugins.radius.password.RadiusCredentialModel;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusConnectionProvider;
import ua.zaskarius.keycloak.plugins.radius.radius.provider.RadiusRadiusProvider;

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
        if (hasPasswordReadPermission(realm, userModel)) {
            List<CredentialModel> credentials = keycloakSession
                    .userCredentialManager()
                    .getStoredCredentialsByType(realm, userModel, RadiusCredentialModel.TYPE);
            if (!credentials.isEmpty()) {
                CredentialModel credentialModel = credentials.get(0);
                RadiusCredentialModel model = RadiusCredentialModel
                        .createFromCredentialModel(credentialModel);
                return model.getSecret().getPassword();
            }
        }
        return null;
    }

    public static boolean hasPasswordReadPermission(
            RealmModel realm,
            UserModel userModel
    ) {
        RoleModel role = realm
                .getRole(RadiusRadiusProvider.READ_RADIUS_PASSWORD);
        return role != null &&
                userModel.isEnabled()
                && userModel
                .hasRole(role);
    }

    public static String getPassword(
            KeycloakSession keycloakSession,
            RealmModel realm,
            UserModel userModel
    ) {
        if (!hasPasswordReadPermission(realm, userModel)) {
            throw new IllegalStateException(userModel.getUsername() +
                    " does not have role " +
                    RadiusRadiusProvider.READ_RADIUS_PASSWORD);
        }
        String currentPassword = getCurrentPassword(keycloakSession, realm, userModel);
        if (currentPassword == null) {
            throw new IllegalStateException(userModel.getUsername() +
                    " does not have radius password");
        }
        return currentPassword;
    }

    public static IRadiusConnectionProvider getProvider(KeycloakSession session,
                                                        RealmModel realm) {
        IRadiusConfiguration config = RadiusConfigHelper
                .getConfig();
        RadiusCommonSettings commonSettings = config
                .getCommonSettings(realm);
        return session
                .getProvider(IRadiusConnectionProvider.class,
                        commonSettings.getProvider());
    }

    public static boolean isUseRadius(RealmModel realm) {
        IRadiusConfiguration config = RadiusConfigHelper.getConfig();
        return config.isUsedRadius(realm);
    }

}
