package ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols;

import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import ua.zaskarius.keycloak.plugins.radius.event.log.EventLoggerUtils;
import ua.zaskarius.keycloak.plugins.radius.radius.holder.IRadiusUserInfoGetter;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAttributeProvider;
import ua.zaskarius.keycloak.plugins.radius.radius.RadiusLibraryUtils;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.KeycloakAttributesType;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.clientconnection.RadiusClientConnection;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.session.KeycloakSessionUtils;

import java.net.InetSocketAddress;
import java.util.Set;

public abstract class AbstractAuthProtocol implements AuthProtocol {
    protected final AccessRequest accessRequest;
    protected final KeycloakSession session;

    public AbstractAuthProtocol(AccessRequest accessRequest, KeycloakSession session) {
        this.accessRequest = accessRequest;
        this.session = session;
    }


    @Override
    public RealmModel getRealm() {
        return RadiusLibraryUtils.getRealm(session, accessRequest);
    }

    protected abstract void answer(RadiusPacket answer,
                                   IRadiusUserInfoGetter radiusUserInfoGetter);


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
        answer.addAttribute("Acct-Interim-Interval", "60");
        IRadiusUserInfoGetter radiusUserInfoGetter = KeycloakSessionUtils
                .getRadiusUserInfo(session);
        if (radiusUserInfoGetter != null) {
            Set<IRadiusAttributeProvider> providers = session
                    .getAllProviders(IRadiusAttributeProvider.class);
            for (IRadiusAttributeProvider provider : providers) {
                for (KeycloakAttributesType attributesType : KeycloakAttributesType.values()) {
                    prepareAnswerAttributes(provider, attributesType, answer);
                }
            }
            answer(answer, radiusUserInfoGetter);
        }
    }

    protected boolean isValidProtocol() {
        return true;
    }

    @Override
    public final boolean isValid(InetSocketAddress address) {
        boolean isValid = getRealm() != null && isValidProtocol();
        if (!isValid) {
            EventBuilder event = EventLoggerUtils
                    .createMasterEvent(session,
                            new RadiusClientConnection(address, accessRequest));
            event.event(EventType.LOGIN).detail(EventLoggerUtils.RADIUS_MESSAGE,
                    "Protocol " + getClass() + " is not valid.")
                    .error("Please set Realm name in radius configuration");
        }
        return isValid;
    }

    @Override
    public AccessRequest getAccessRequest() {
        return accessRequest.copy();
    }
}
