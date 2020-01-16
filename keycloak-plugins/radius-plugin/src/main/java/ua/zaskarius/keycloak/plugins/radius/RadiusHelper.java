package ua.zaskarius.keycloak.plugins.radius;

import com.google.common.annotations.VisibleForTesting;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import ua.zaskarius.keycloak.plugins.radius.configuration.IRadiusConfiguration;
import ua.zaskarius.keycloak.plugins.radius.configuration.RadiusConfigHelper;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import ua.zaskarius.keycloak.plugins.radius.password.RadiusCredentialModel;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusDictionaryProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServiceProvider;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public final class RadiusHelper {

    private static List<String> realmAttributes = new ArrayList<>();

    private static Map<String, List<IRadiusServiceProvider>> serviceMap = new HashMap<>();

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

    public static List<String> getRealmAttributes(KeycloakSession session) {
        if (realmAttributes.isEmpty()) {
            Set<IRadiusDictionaryProvider> providers = session
                    .getAllProviders(IRadiusDictionaryProvider.class);
            for (IRadiusDictionaryProvider provider : providers) {
                List<String> attributes = provider.getRealmAttributes();
                if (attributes != null) {
                    realmAttributes.addAll(attributes);
                }
            }
        }

        return realmAttributes;
    }


    public static boolean isUseRadius() {
        IRadiusConfiguration config = RadiusConfigHelper.getConfig();
        RadiusServerSettings radiusSettings = config.getRadiusSettings();
        return radiusSettings
                .isUseUdpRadius() ||
                radiusSettings
                        .getRadSecSettings()
                        .isUseRadSec();
    }


    public static Map<String, List<IRadiusServiceProvider>> getServiceMap(
            KeycloakSession session) {
        if (serviceMap.isEmpty()) {
            Set<IRadiusServiceProvider> allProviders = session
                    .getAllProviders(IRadiusServiceProvider.class);
            for (IRadiusServiceProvider provider : allProviders) {
                String attrbuteName = provider.attrbuteName();
                List<IRadiusServiceProvider> serviceProviders = serviceMap
                        .get(attrbuteName);
                if (serviceProviders == null) {
                    serviceProviders = new ArrayList<>();
                    serviceMap.put(attrbuteName, serviceProviders);
                }
                serviceProviders.add(provider);
            }
        }
        return serviceMap;
    }

    @VisibleForTesting
    public static Map<String, List<IRadiusServiceProvider>> getServiceMap0() {
        return serviceMap;
    }

    @VisibleForTesting
    public static void setRealmAttributes(List<String> realmAttributes) {
        RadiusHelper.realmAttributes = realmAttributes;
    }
}
