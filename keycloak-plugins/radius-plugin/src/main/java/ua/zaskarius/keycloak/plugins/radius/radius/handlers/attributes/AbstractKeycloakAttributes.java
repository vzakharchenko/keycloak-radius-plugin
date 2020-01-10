package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import ua.zaskarius.keycloak.plugins.radius.event.log.EventLoggerFactory;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusAttributeHolder;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusUserInfo;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals.AttributeConditional;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.session.KeycloakSessionUtils;
import org.jboss.logging.Logger;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.attribute.AttributeType;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractKeycloakAttributes<KEYCLOAK_TYPE> implements KeycloakAttributes {

    public static final String RADIUS_ATTRIBUTES = "Radius Attributes";
    private static final Logger LOGGER = Logger.getLogger(AbstractKeycloakAttributes.class);
    protected final KeycloakSession session;
    protected final RadiusUserInfo radiusUserInfo;

    private List<RadiusAttributeHolder<KEYCLOAK_TYPE>> attributeHolders = new ArrayList<>();

    public AbstractKeycloakAttributes(KeycloakSession session) {
        this.session = session;
        radiusUserInfo = KeycloakSessionUtils.getRadiusUserInfo(session);
    }

    protected abstract KeycloakAttributesType getType();

    protected abstract Set<KEYCLOAK_TYPE> getKeycloakTypes();

    protected abstract List<String> getAttributes(KEYCLOAK_TYPE type,
                                                  String attributeName);

    @Override
    public KeycloakAttributes read() {
        Set<KEYCLOAK_TYPE> groups = getKeycloakTypes();
        for (KEYCLOAK_TYPE type : groups) {
            List<String> radiusAttributes = getAttributes(type, RADIUS_ATTRIBUTES);
            if (radiusAttributes != null) {
                for (String radiusAttribute : radiusAttributes) {
                    RadiusAttributeHolder<KEYCLOAK_TYPE> attributeHolder =
                            new RadiusAttributeHolder<>(getType(), type);
                    attributeHolder.addAttribute(radiusAttribute,
                            getAttributes(type, radiusAttribute));
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
            EventBuilder event = EventLoggerFactory
                    .createEvent(session, radiusUserInfo.getRealmModel(), radiusUserInfo
                            .getClientConnection());

            event.event(EventType
                    .CLIENT_INFO_ERROR)
                    .detail(EventLoggerFactory.RADIUS_MESSAGE,
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

    protected abstract List<AttributeConditional<KEYCLOAK_TYPE>> getAttributeConditional();

    @Override
    public KeycloakAttributes filter(AccessRequest accessRequest) {
        this.attributeHolders = this.attributeHolders.stream().filter(rh -> {
            boolean useRadiusAttributes = true;
            List<AttributeConditional<KEYCLOAK_TYPE>> attributeConditionals
                    = getAttributeConditional();
            if (attributeConditionals != null) {
                for (AttributeConditional<KEYCLOAK_TYPE>
                        conditional : attributeConditionals) {
                    useRadiusAttributes &= conditional
                            .useAttributes(rh, accessRequest);
                }
            }
            return useRadiusAttributes;
        }).collect(Collectors.toList());

        return this;
    }

    @Override
    public void fillAnswer(RadiusPacket answer) {
        for (RadiusAttributeHolder<KEYCLOAK_TYPE> attributeHolder : attributeHolders) {
            Map<String, List<String>> attributes = attributeHolder.getAttributes();
            for (Map.Entry<String, List<String>> entry : attributes.entrySet()) {
                String attributeName = entry.getKey();
                for (String value : entry.getValue()) {
                    answer.addAttribute(attributeName, value);
                }
            }
        }
    }
}
