package ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols;

import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import ua.zaskarius.keycloak.plugins.radius.RadiusHelper;
import ua.zaskarius.keycloak.plugins.radius.event.log.EventLoggerFactory;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusUserInfo;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAttributeProvider;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.KeycloakAttributesType;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.clientconnection.RadiusClientConnection;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.session.KeycloakSessionUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

public abstract class AbstractAuthProtocol implements AuthProtocol {
    protected final AccessRequest accessRequest;
    protected final KeycloakSession session;

    public AbstractAuthProtocol(AccessRequest accessRequest, KeycloakSession session) {
        this.accessRequest = accessRequest;
        this.session = session;
    }

    private String getRealmName(String attributeName) {
        RadiusAttribute attribute = accessRequest.getAttribute(attributeName);
        return (attribute != null) ? attribute.getValueString() : null;
    }

    private RealmModel getRealm(List<String> attributes) {
        for (String attribute : attributes) {
            String realmName = getRealmName(attribute);
            if (realmName != null) {
                return session.realms().getRealm(realmName);
            }
        }
        return null;
    }

    @Override
    public RealmModel getRealm() {
        List<String> attributes = RadiusHelper.getRealmAttributes(session);
        return getRealm(attributes);
    }

    protected abstract void answer(RadiusPacket answer, RadiusUserInfo radiusUserInfo);


    private void prepareAnswerAttributes(IRadiusAttributeProvider provider,
                                         KeycloakAttributesType attributesType,
                                         RadiusPacket answer) {
        provider
                .createKeycloakAttributes(accessRequest, session, attributesType).read()
                .ignoreUndefinedAttributes(answer
                        .getDictionary())
                .fillAnswer(answer);
    }

    @Override
    public final void prepareAnswer(RadiusPacket answer) {
        RadiusUserInfo radiusUserInfo = KeycloakSessionUtils.getRadiusUserInfo(session);
        if (radiusUserInfo != null) {
            Set<IRadiusAttributeProvider> providers = session
                    .getAllProviders(IRadiusAttributeProvider.class);
            for (IRadiusAttributeProvider provider : providers) {
                for (KeycloakAttributesType attributesType : KeycloakAttributesType.values()) {
                    prepareAnswerAttributes(provider, attributesType, answer);
                }
            }
            answer(answer, radiusUserInfo);
        }
    }

    protected boolean isValidProtocol() {
        return true;
    }

    @Override
    public final boolean isValid(InetSocketAddress address) {
        boolean isValid = getRealm() != null && isValidProtocol();
        if (!isValid) {
            EventBuilder event = EventLoggerFactory
                    .createMasterEvent(session,
                            new RadiusClientConnection(address));
            event.event(EventType.LOGIN).detail(EventLoggerFactory.RADIUS_MESSAGE,
                    "Protocol " + getClass() + " is not valid.")
                    .error("Please set Realm name in radius configuration");
        }
        return isValid;
    }
}
