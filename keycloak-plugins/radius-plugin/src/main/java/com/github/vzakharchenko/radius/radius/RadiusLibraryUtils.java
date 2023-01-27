package com.github.vzakharchenko.radius.radius;

import com.github.vzakharchenko.radius.event.log.EventLoggerUtils;
import com.github.vzakharchenko.radius.models.Attribute26Holder;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.keycloak.authentication.authenticators.client.ClientIdAndSecretAuthenticator;
import org.keycloak.common.ClientConnection;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.util.JsonSerialization;
import org.tinyradius.attribute.AttributeType;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.RadiusPacket;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

import static com.github.vzakharchenko.radius.client.RadiusLoginProtocolFactory.RADIUS_PROTOCOL;

public final class RadiusLibraryUtils {

    private static final Logger LOGGER = Logger.getLogger(RadiusLibraryUtils.class);

    private RadiusLibraryUtils() {
    }

    private static void replaceTypeType(AttributeType attributeType, int type) {
        try {
            Field typeCode = AttributeType.class.getDeclaredField("typeCode");
            try {
                typeCode.setAccessible(true);
                typeCode.set(attributeType, type);
            } finally {
                typeCode.setAccessible(false);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static RadiusAttribute get26Attribute(
            Dictionary dictionary,
            Attribute26Holder attribute26Holder) {
        AttributeType attributeType = new AttributeType(attribute26Holder.getVendor(),
                254, attribute26Holder.getAttributeName(),
                "string");
        replaceTypeType(attributeType, attribute26Holder.getNewType());
        return attributeType
                .create(dictionary, attribute26Holder.getValue());
    }

    public static String getRealUserName(String username, RealmModel realm) {
        return StringUtils.removeEnd(username, "@" + realm.getName());
    }

    public static UserModel getUserByUsername(KeycloakSession localSession,
                                              String username, RealmModel realm) {
        UserModel user = localSession.users().getUserByUsername(realm, username);
        if (user == null) {
            user = localSession.users().getUserByUsername(realm, getRealUserName(username, realm));
        }
        return user;
    }

    public static UserModel getServiceAccount(KeycloakSession localSession,
                                              String username, RealmModel realm) {
        ClientModel clientModel = realm.getClientsStream()
                .filter(cm -> cm.getClientId().equalsIgnoreCase(username) ||
                        cm.getClientId().equalsIgnoreCase(getRealUserName(username, realm)))
                .findFirst().orElse(null);

        return clientModel != null ? localSession.users().getServiceAccount(clientModel) : null;
    }

    public static UserModel getUserByEmail(KeycloakSession localSession,
                                           String username, RealmModel realm) {
        UserModel user = localSession.users().getUserByEmail(realm, username);
        if (user == null) {
            user = localSession.users().getUserByEmail(realm, getRealUserName(username, realm));
        }
        return user;
    }

    public static UserModel getUserModel(KeycloakSession localSession,
                                         String username, RealmModel realm) {
        UserModel user = getUserByUsername(localSession, username, realm);
        if (user == null) {
            user = getUserByEmail(localSession, username, realm);
        }
        return user == null ? getServiceAccount(localSession, username, realm) : user;
    }

    public static String getUserName(RadiusPacket radiusPacket) {
        final RadiusAttribute attribute = radiusPacket.getAttribute(1);
        return attribute == null ?
                "" : attribute.getValueString();
    }

    public static void setUserName(RadiusPacket radiusPacket, String userName) {
        radiusPacket.addAttribute("User-Name", userName);
    }

    public static ClientModel getClient(ClientConnection clientConnection,
                                        KeycloakSession session,
                                        RealmModel realmModel) {
        ClientModel client = realmModel.getClientsStream()
                .filter(c -> Objects.equals(c.getProtocol(), RADIUS_PROTOCOL))
                .findFirst().orElse(null);
        if (client == null) {
            EventBuilder event = EventLoggerUtils.createEvent(session, realmModel,
                    clientConnection);
            LOGGER.error("Client with radius protocol does not found");
            event.event(EventType.LOGIN_ERROR)
                    .detail(EventLoggerUtils.RADIUS_MESSAGE,
                            "Client with radius protocol does not found")
                    .error("Client with radius protocol does not found");
        }
        return client;
    }

    public static String getAttributeValue(RadiusPacket radiusPacket, String attributeName) {
        String attributeValue = radiusPacket.getAttributeValue(attributeName);
        return attributeValue == null ? "" : attributeValue;
    }

    public static byte[] getOrEmpty(byte[] data, int size) {
        return data != null ? data : new byte[size];
    }

    public static String writeValueAsString(Object obj) {
        try {
            return JsonSerialization.writeValueAsString(obj);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String getServiceAccountPassword(UserModel userModel,
                                                   RealmModel realmModel) {
        if (userModel != null && userModel.isEnabled()
                && userModel.getServiceAccountClientLink() != null) {
            ClientModel clientModel = realmModel.
                    getClientById(userModel
                            .getServiceAccountClientLink());
            return ClientIdAndSecretAuthenticator.PROVIDER_ID.equals(
                    clientModel.getClientAuthenticatorType()
            ) ?
                    clientModel.getSecret() : null;
        }
        return null;
    }
}
