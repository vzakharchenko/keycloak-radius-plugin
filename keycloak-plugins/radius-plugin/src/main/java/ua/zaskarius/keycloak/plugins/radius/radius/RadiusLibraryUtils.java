package ua.zaskarius.keycloak.plugins.radius.radius;

import org.jboss.logging.Logger;
import org.keycloak.common.ClientConnection;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.tinyradius.attribute.AttributeType;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.RadiusPacket;
import ua.zaskarius.keycloak.plugins.radius.RadiusHelper;
import ua.zaskarius.keycloak.plugins.radius.event.log.EventLoggerUtils;
import ua.zaskarius.keycloak.plugins.radius.models.Attribute26Holder;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static ua.zaskarius.keycloak.plugins.radius.client.RadiusLoginProtocolFactory.RADIUS_PROTOCOL;

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


    private static String getRealmName(String attributeName, RadiusPacket radiusPacket) {
        RadiusAttribute attribute = radiusPacket.getAttribute(attributeName);
        return (attribute != null) ? attribute.getValueString() : null;
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
        return null;
    }

    public static RealmModel getRealm(KeycloakSession session, RadiusPacket radiusPacket) {
        List<String> attributes = RadiusHelper.getRealmAttributes(session);
        return getRealm(session, radiusPacket, attributes);
    }


    public static UserModel getUserModel(KeycloakSession localSession,
                                         String username, RealmModel realm) {
        UserModel user = localSession.users().getUserByUsername(username, realm);
        if (user == null) {
            user = localSession.users().getUserByEmail(username, realm);
        }
        return user;
    }

    public static String getUserName(RadiusPacket radiusPacket) {
        final RadiusAttribute attribute = radiusPacket.getAttribute(1);
        return attribute == null ?
                null : attribute.getValueString();
    }

    public static ClientModel getClient(ClientConnection clientConnection,
                                        KeycloakSession session,
                                        RealmModel realmModel) {
        List<ClientModel> clients = realmModel.getClients();
        for (ClientModel client : clients) {
            if (Objects.equals(client.getProtocol(), RADIUS_PROTOCOL)) {
                return client;
            }
        }
        EventBuilder event = EventLoggerUtils
                .createEvent(session, realmModel,
                        clientConnection);
        LOGGER.error("Client with radius protocol does not found");
        event.event(EventType.LOGIN_ERROR).detail(
                EventLoggerUtils.RADIUS_MESSAGE, "Client with radius protocol does not found")
                .error("Client with radius protocol does not found");
        return null;
    }

    public static String getAttributeValue(RadiusPacket radiusPacket, String attributeName) {
        String attributeValue = radiusPacket.getAttributeValue(attributeName);
        return attributeValue == null ? "" : attributeValue;
    }
}
