package com.github.vzakharchenko.radius.proxy.proxy;

import com.github.vzakharchenko.radius.proxy.client.RadiusProxyClientHelper;
import com.github.vzakharchenko.radius.proxy.providers.IRadiusProxyEndpointProvider;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.packet.RadiusPackets;
import org.tinyradius.util.RadiusEndpoint;

import java.util.Collection;

import static org.tinyradius.packet.PacketType.ACCESS_REJECT;

public class RadiusComponentProxy implements IComponentProxy {

    private static final Logger LOGGER = Logger.getLogger(RadiusComponentProxy.class);

    private RadiusEndpoint getRadiusEndpoint(KeycloakSession session,
                                             Collection<IRadiusProxyEndpointProvider> providers,
                                             RadiusPacket t) {
        for (IRadiusProxyEndpointProvider provider : providers) {
            RadiusEndpoint radiusEndpoint = provider.getRadiusEndpoint(session, t.getClass());
            if (radiusEndpoint != null) {
                return radiusEndpoint;
            }
        }
        return null;
    }

    protected RadiusEndpoint getRadiusEndpoint(KeycloakSession session, RadiusPacket t) {
        return getRadiusEndpoint(session,
                session.getAllProviders(IRadiusProxyEndpointProvider.class),
                t);
    }

    @Override
    public RadiusPacket proxy(KeycloakSession session, RadiusPacket radiusPacket,
                              RadiusPacket answer) {
        RadiusEndpoint radiusEndpoint = getRadiusEndpoint(session, radiusPacket);
        Dictionary dictionary = radiusPacket.getDictionary();
        if (radiusEndpoint != null) {
            return RadiusProxyClientHelper.requestProxy(dictionary, radiusClient -> {
                RadiusPacket proxyAnswer = radiusClient.communicate(radiusPacket,
                        radiusEndpoint).syncUninterruptibly().getNow();
                return answerHandler(answer, proxyAnswer);
            }, ex -> {
                LOGGER.error("proxy request failed: " + radiusEndpoint.getAddress(), ex);
                RadiusPacket answerPacket = RadiusPackets
                        .create(dictionary, ACCESS_REJECT, answer.getIdentifier());
                answer.getAttributes().forEach(answerPacket::addAttribute);
                return answerPacket;
            });
        } else {
            return answer;
        }
    }

    protected RadiusPacket answerHandler(RadiusPacket answer,
                                         RadiusPacket proxyAnswer) {
        RadiusPacket radiusPacket = RadiusPackets.create(answer.getDictionary(),
                proxyAnswer.getType(), answer.getIdentifier());
        answer.getAttributes().forEach(radiusPacket::addAttribute);
        proxyAnswer.getAttributes().forEach(radiusPacket::addAttribute);
        return radiusPacket;
    }
}
