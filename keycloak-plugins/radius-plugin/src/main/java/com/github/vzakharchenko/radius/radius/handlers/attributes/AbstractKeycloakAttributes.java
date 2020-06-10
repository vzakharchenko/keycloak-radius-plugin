package com.github.vzakharchenko.radius.radius.handlers.attributes;

import com.github.vzakharchenko.radius.RadiusHelper;
import com.github.vzakharchenko.radius.models.RadiusAttributeHolder;
import com.github.vzakharchenko.radius.providers.IRadiusServiceProvider;
import com.github.vzakharchenko.radius.radius.handlers.session.KeycloakSessionUtils;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoGetter;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.attribute.AttributeType;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;

import java.util.*;

public abstract class AbstractKeycloakAttributes<KEYCLOAK_TYPE> implements KeycloakAttributes {

    private static final Logger LOGGER = Logger.getLogger(AbstractKeycloakAttributes.class);
    public static final String REJECT_CONDITIONS = "REJECT_";
    public static final String CONDITION = "COND_";
    public static final String ACCEPT_CONDITION = "ACCEPT_";
    public static final String REJECT_RADIUS = "REJECT_RADIUS";
    protected final KeycloakSession session;
    protected final IRadiusUserInfoGetter radiusUserInfoGetter;
    protected final AccessRequest accessRequest;

    private final List<RadiusAttributeHolder<KEYCLOAK_TYPE>> attributeHolders = new ArrayList<>();

    public AbstractKeycloakAttributes(KeycloakSession session,
                                      AccessRequest accessRequest) {
        this.session = session;
        this.accessRequest = accessRequest;
        this.radiusUserInfoGetter = KeycloakSessionUtils.getRadiusUserInfo(session);
    }


    private boolean clearServiceAttributes(Set<String> serviceStrings,
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
        if (conditionalAttributes(attributes)) {
            return filterServiceAttributes(attributes);
        }
        if (attributes.get(REJECT_RADIUS) != null){
          radiusUserInfoGetter.getBuilder().forceReject();
        }
        return new HashMap<>();
    }

    private boolean isValidConditional0(String radiusAttributeName, Collection<String> values) {
        RadiusAttribute attribute = accessRequest.getAttribute(radiusAttributeName);
        if (attribute == null) {
            return false;
        }
        return values.stream().anyMatch(s -> Arrays
                .stream(s.split(",")).anyMatch(s1 ->
                        Objects.equals(s1, attribute.getValueString())));
    }

    private boolean isValidConditional(String radiusAttributeName, Collection<String> values) {
        if (testAttribute(radiusAttributeName, accessRequest.getDictionary())) {
            return isValidConditional0(radiusAttributeName, values);
        }
        return true;
    }

    protected boolean conditionalAttributes(
            String prefix,
            String attributeName,
            Set<String> values, boolean defaultResult) {
        if (attributeName.toUpperCase(Locale.US).startsWith(prefix)) {
            String radiusAttributeName = attributeName
                    .replaceFirst("(?i)" + prefix, "");
            return isValidConditional(radiusAttributeName, values);
        }
        return defaultResult;
    }

    protected boolean conditionalAttributes(Map<String, Set<String>> attributes) {
        for (Map.Entry<String, Set<String>> entry : attributes.entrySet()) {
            String attributeName = entry.getKey();
            Set<String> values = entry.getValue();
            boolean r = !conditionalAttributes(REJECT_CONDITIONS,
                    attributeName, values, false) &&
                    conditionalAttributes(ACCEPT_CONDITION,
                            attributeName, values, true);
            if (!r) {
                radiusUserInfoGetter.getBuilder().forceReject();
                return false;
            }
            if (!conditionalAttributes(CONDITION, attributeName, values, true)) {
                return false;
            }

        }
        return true;
    }

    protected Map<String, Set<String>> filterServiceAttributes(Map<String,
            Set<String>> attributes) {
        Map<String, List<IRadiusServiceProvider>> serviceMap = RadiusHelper
                .getServiceMap(session);
        for (Map.Entry<String, List<IRadiusServiceProvider>> entry : serviceMap.entrySet()) {
            String attributeName = entry.getKey();
            List<IRadiusServiceProvider> providers = entry.getValue();
            Set<String> serviceStrings = attributes.get(attributeName);

            if (clearServiceAttributes(serviceStrings, providers)) {
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
            LOGGER.trace(message);
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
