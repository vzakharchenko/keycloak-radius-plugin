package ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols;

import ua.zaskarius.keycloak.plugins.radius.event.log.EventLoggerFactory;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusUserInfo;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAttributeProvider;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.KeycloakAttributesType;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.clientconnection.RadiusClientConnection;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.session.KeycloakSessionUtils;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;

import java.net.InetSocketAddress;
import java.util.Set;

public abstract class AbstractAuthProtocol implements AuthProtocol {
    protected final AccessRequest accessRequest;
    protected final KeycloakSession session;

    public AbstractAuthProtocol(AccessRequest accessRequest, KeycloakSession session) {
        this.accessRequest = accessRequest;
        this.session = session;
    }

    private String getRealmName() {
        RadiusAttribute attribute = accessRequest.getAttribute(VendorUtils.MIKROTIK_VENDOR,
                VendorUtils.MIKROTIK_REALM);
        return (attribute != null) ? attribute.getValueString() : null;
    }

    @Override
    public RealmModel getRealm() {
        String realmName = getRealmName();
        if (realmName != null) {
            return session.realms().getRealm(realmName);
        } else {
            return null;
        }
    }

    protected abstract void answer(RadiusPacket answer, RadiusUserInfo radiusUserInfo);

    @Override
    public final void prepareAnswer(RadiusPacket answer) {
        RadiusUserInfo radiusUserInfo = KeycloakSessionUtils.getRadiusUserInfo(session);
        if (radiusUserInfo != null) {
            Set<IRadiusAttributeProvider> providers = session
                    .getAllProviders(IRadiusAttributeProvider.class);
            for (IRadiusAttributeProvider provider : providers) {
                provider
                        .createKeycloakAttributes(session, KeycloakAttributesType
                                .GROUP).read()
                        .ignoreUndefinedAttributes(answer
                                .getDictionary())
                        .filter(accessRequest)
                        .fillAnswer(answer);
                provider
                        .createKeycloakAttributes(session, KeycloakAttributesType
                                .ROLE).read()
                        .ignoreUndefinedAttributes(answer
                                .getDictionary())
                        .filter(accessRequest)
                        .fillAnswer(answer);
                provider
                        .createKeycloakAttributes(session, KeycloakAttributesType
                                .USER).read()
                        .ignoreUndefinedAttributes(answer
                                .getDictionary())
                        .filter(accessRequest)
                        .fillAnswer(answer);
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
