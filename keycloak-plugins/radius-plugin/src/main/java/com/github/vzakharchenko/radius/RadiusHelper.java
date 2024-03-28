package com.github.vzakharchenko.radius;

import com.github.vzakharchenko.radius.client.RadiusLoginProtocolFactory;
import com.github.vzakharchenko.radius.configuration.IRadiusConfiguration;
import com.github.vzakharchenko.radius.configuration.RadiusConfigHelper;
import com.github.vzakharchenko.radius.models.RadiusServerSettings;
import com.github.vzakharchenko.radius.password.RadiusCredentialModel;
import com.github.vzakharchenko.radius.password.UpdateRadiusPassword;
import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProvider;
import com.github.vzakharchenko.radius.providers.IRadiusServiceProvider;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.packet.RadiusPacket;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public final class RadiusHelper {

    public static final int MAX_PASSWORD_SIZE = 14;
    private static final char[] PSEUDO = {
            '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f', 'g', 'h', 'i', 'j', 'k', 'm',
            'n', 'o', 'p', 'q', 'r', 'u', 's',
            't', 'v', 'w', 'x', 'y', 'z', 'A',
            'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z'};
    public static final int DEFAULT_REALM = 1;

    private static List<String> realmAttributes = new ArrayList<>();

    private static Map<String, List<IRadiusServiceProvider>> serviceMap = new HashMap<>();

    private RadiusHelper() {
    }

    public static String generatePassword() {

        StringBuilder out = new StringBuilder(MAX_PASSWORD_SIZE);
        byte[] in = new byte[MAX_PASSWORD_SIZE];
        getSecureRandom().nextBytes(in);
        for (int i = 0; i < MAX_PASSWORD_SIZE; i++) {
            out.append(PSEUDO[((char) in[i]) % PSEUDO.length]);
        }
        return out.toString();

    }


    public static SecureRandom getSecureRandom() {
        return getSecureRandom("NativePRNGNonBlocking");
    }

    public static SecureRandom getSecureRandom(String alg) {
        try {
            return SecureRandom.getInstance(alg);
        } catch (NoSuchAlgorithmException e) {
            return new SecureRandom();
        }
    }

    public static byte getRandomByte() {
        SecureRandom secureRandom = getSecureRandom();
        byte[] bytes = new byte[1];
        secureRandom.nextBytes(bytes);
        return bytes[0];
    }

    public static String getCurrentPassword(
            UserModel userModel
    ) {
        List<CredentialModel> credentials = userModel.credentialManager()
                .getStoredCredentialsByTypeStream(RadiusCredentialModel.TYPE)
                .collect(Collectors.toList());
        if (userModel.getRequiredActionsStream()
                .noneMatch(rAction -> Objects.equals(rAction,
                        UpdateRadiusPassword.RADIUS_UPDATE_PASSWORD) ||
                        Objects.equals(rAction,
                                UserModel.RequiredAction.UPDATE_PASSWORD.name())) &&
                !credentials.isEmpty()) {
            CredentialModel credentialModel = credentials.get(0);
            RadiusCredentialModel model = RadiusCredentialModel
                    .createFromCredentialModel(credentialModel);
            return model.getSecret().getPassword();
        }
        return null;
    }

    public static String getPassword(
            UserModel userModel
    ) {
        String currentPassword = getCurrentPassword(userModel);
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

    private static List<String> getRealmAttributes(KeycloakSession session) {
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
                String attributeName = provider.attributeName();
                List<IRadiusServiceProvider> serviceProviders = serviceMap
                        .get(attributeName);
                if (serviceProviders == null) {
                    serviceProviders = new ArrayList<>();
                    serviceMap.put(attributeName, serviceProviders);
                }
                serviceProviders.add(provider);
            }
        }
        return serviceMap;
    }

    private static String getRealmName(String attributeName, RadiusPacket radiusPacket) {
        RadiusAttribute attribute = radiusPacket.getAttribute(attributeName);
        return (attribute != null) ? attribute.getValueString() : null;
    }

    private static RealmModel getDefaultRealm(KeycloakSession session) {
        List<RealmModel> realms = session.realms()
                .getRealmsStream().filter(realmModel -> realmModel
                        .getClientsStream().anyMatch(clientModel -> Objects.equals(
                                clientModel.getProtocol(),
                                RadiusLoginProtocolFactory.RADIUS_PROTOCOL)))
                .collect(Collectors.toList());
        switch (realms.size()) {
            case 0:
                throw new IllegalStateException("Radius Realm does not exist. " +
                        "Please create at least one realm with radius client");
            case DEFAULT_REALM:
                break;
            default:
                throw new IllegalStateException("Found more than one Radius Realm (" +
                        realms.stream().map(RealmModel::getName)
                                .collect(Collectors.joining(", ")) +
                        "). If you expect to use the Default Realm, " +
                        "than you should use only one realm with radius client");
        }
        return realms.get(0);
    }

    private static RealmModel getRealmFromUserName(KeycloakSession session,
                                                   RadiusPacket radiusPacket) {
        String realmName = StringUtils.substringAfterLast(radiusPacket.getAttribute("User-Name")
                .getValueString(), "@");
        RealmModel realm = null;
        if (StringUtils.isNotEmpty(realmName)) {
            realm = session.realms().getRealm(realmName);
        }
        return (realm == null) ? getDefaultRealm(session) : realm;
    }

    private static RealmModel getRealm(KeycloakSession session,
                                       RadiusPacket radiusPacket,
                                       Collection<String> attributes) {
        for (String attribute : attributes) {
            String realmName = getRealmName(attribute, radiusPacket);
            if (realmName != null) {
                return session.realms().getRealm(realmName);
            }
        }

        return getRealmFromUserName(session, radiusPacket);
    }

    public static RealmModel getRealm(KeycloakSession session, RadiusPacket radiusPacket) {
        List<String> attributes = getRealmAttributes(session);
        return getRealm(session, radiusPacket, attributes);
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
