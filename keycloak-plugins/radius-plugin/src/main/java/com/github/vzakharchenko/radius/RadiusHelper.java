package com.github.vzakharchenko.radius;

import com.github.vzakharchenko.radius.configuration.IRadiusConfiguration;
import com.github.vzakharchenko.radius.configuration.RadiusConfigHelper;
import com.github.vzakharchenko.radius.models.RadiusServerSettings;
import com.github.vzakharchenko.radius.password.RadiusCredentialModel;
import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProvider;
import com.github.vzakharchenko.radius.providers.IRadiusServiceProvider;
import com.google.common.annotations.VisibleForTesting;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public final class RadiusHelper {

    private static List<String> realmAttributes = new ArrayList<>();

    private static Map<String, List<IRadiusServiceProvider>> serviceMap = new HashMap<>();

    private RadiusHelper() {
    }

    public static String generatePassword() {
        try {
            return SecureRandom.getInstanceStrong()
                    .ints(10, 33, 122)
                    .mapToObj(i -> String.valueOf((char) i))
                    .collect(Collectors.joining()).replaceAll("\"", "@");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
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

    private static void realmAttributesInit(KeycloakSession session) {
        Set<IRadiusDictionaryProvider> providers = session
                .getAllProviders(IRadiusDictionaryProvider.class);
        for (IRadiusDictionaryProvider provider : providers) {
            List<String> attributes = provider.getRealmAttributes();
            if (attributes != null) {
                realmAttributes.addAll(attributes);
            }
        }
    }

    public static List<String> getRealmAttributes(KeycloakSession session) {
        if (realmAttributes.isEmpty()) {
            realmAttributesInit(session);
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
                String attrbuteName = provider.attributeName();
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
