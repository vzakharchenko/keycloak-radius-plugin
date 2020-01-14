package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import org.jboss.logging.Logger;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.attribute.AttributeType;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import ua.zaskarius.keycloak.plugins.radius.RadiusHelper;
import ua.zaskarius.keycloak.plugins.radius.event.log.EventLoggerUtils;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusAttributeHolder;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusUserInfo;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServiceProvider;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.session.KeycloakSessionUtils;

import java.util.*;

public abstract class AbstractKeycloakAttributes<KEYCLOAK_TYPE> implements KeycloakAttributes {

    private static final Logger LOGGER = Logger.getLogger(AbstractKeycloakAttributes.class);
    protected final KeycloakSession session;
    protected final RadiusUserInfo radiusUserInfo;
    protected final AccessRequest accessRequest;

    private final List<RadiusAttributeHolder<KEYCLOAK_TYPE>> attributeHolders = new ArrayList<>();

    public AbstractKeycloakAttributes(KeycloakSession session,
                                      AccessRequest accessRequest) {
        this.session = session;
        this.accessRequest = accessRequest;
        radiusUserInfo = KeycloakSessionUtils.getRadiusUserInfo(session);
    }


    private boolean clearAttributes(Set<String> serviceStrings,
                                    Collection<IRadiusServiceProvider> providers) {
        if (serviceStrings != null) {
            for (String serviceString : serviceStrings) {
                String[] services = serviceString.split(",");
                for (String service : services) {
                    IRadiusServiceProvider provider = providers.stream().filter(
                            iRadiusServiceProvider ->
                                    service.equalsIgnoreCase(
                                            iRadiusServiceProvider.serviceName()))
                            .findFirst().orElse(null);
                    if (provider != null) {
                        return !provider.checkService(accessRequest);
                    }
                }
            }
        }
        return false;
    }

    protected Map<String, Set<String>> filter(Map<String, Set<String>> attributes) {
        Map<String, List<IRadiusServiceProvider>> serviceMap = RadiusHelper
                .getServiceMap(session);
        for (Map.Entry<String, List<IRadiusServiceProvider>> entry : serviceMap.entrySet()) {
            String attributeName = entry.getKey();
            List<IRadiusServiceProvider> providers = entry.getValue();
            Set<String> serviceStrings = attributes.get(attributeName);

            if (clearAttributes(serviceStrings, providers)) {
                return new HashMap<>();
            }
        }
        return attributes;
    }

    protected abstract KeycloakAttributesType getType();

    protected abstract Set<KEYCLOAK_TYPE> getKeycloakTypes();

    protected abstract Map<String, Set<String>> getAttributes(KEYCLOAK_TYPE type);

    @Override
    public KeycloakAttributes read() {
        Set<KEYCLOAK_TYPE> groups = getKeycloakTypes();
        for (KEYCLOAK_TYPE type : groups) {
            Map<String, Set<String>> attributes = getAttributes(type);
            if (attributes != null) {
                for (Map.Entry<String, Set<String>> entry : attributes.entrySet()) {
                    RadiusAttributeHolder<KEYCLOAK_TYPE> attributeHolder =
                            new RadiusAttributeHolder<>(getType(), type);
                    attributeHolder.addAttribute(entry.getKey(), entry.getValue());
                    attributeHolders.add(attributeHolder);
                }
            }
        }
        return this;
    }


    private boolean testAttribute(String attributeName, Dictionary dictionary) {
        AttributeType attributeType = dictionary.getAttributeTypeByName(attributeName);
        if (attributeType == null) {
            String message = "Attribute " + attributeName + " does not exists";
            LOGGER.warn(message);
            EventBuilder event = EventLoggerUtils
                    .createEvent(session, radiusUserInfo.getRealmModel(), radiusUserInfo
                            .getClientConnection());

            event.event(EventType
                    .CLIENT_INFO_ERROR)
                    .detail(EventLoggerUtils.RADIUS_MESSAGE,
                            message)
                    .user(radiusUserInfo
                            .getUserModel())
                    .error(message);
            return false;
        }
        return true;
    }

    @Override
    public KeycloakAttributes ignoreUndefinedAttributes(Dictionary dictionary) {
        for (RadiusAttributeHolder<KEYCLOAK_TYPE> attributeHolder : attributeHolders) {
            attributeHolder.filter(entry -> entry.getValue() != null &&
                    !entry.getValue().isEmpty() &&
                    testAttribute(entry.getKey(), dictionary));
        }
        return this;
    }

    @Override
    public void fillAnswer(RadiusPacket answer) {
        for (RadiusAttributeHolder<KEYCLOAK_TYPE> attributeHolder : attributeHolders) {
            Map<String, Set<String>> attributes = attributeHolder.getAttributes();
            for (Map.Entry<String, Set<String>> entry : attributes.entrySet()) {
                String attributeName = entry.getKey();
                for (String value : entry.getValue()) {
                    answer.addAttribute(attributeName, value);
                }
            }
        }
    }
}
