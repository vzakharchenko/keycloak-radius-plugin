package com.github.vzakharchenko.radius.radius.server;

import com.github.vzakharchenko.radius.providers.AbstractRadiusServerProviderFactory;
import com.github.vzakharchenko.radius.providers.IRadiusServerProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class RadiusServerProviderFactory
        extends AbstractRadiusServerProviderFactory {

    public static final String RADIUS_PROVIDER = "radius-provider";

    @Override
    public String getId() {
        return RADIUS_PROVIDER;
    }

    @Override
    protected IRadiusServerProvider createInstance(KeycloakSession session) {
        return new KeycloakRadiusServer(session);
    }

    @Override
    public void postInit(KeycloakSession session, IRadiusServerProvider serverProvider) {
        session.getKeycloakSessionFactory().register(event -> {
            if (event instanceof RealmModel.RealmPostCreateEvent postCreateEvent) {
                serverProvider.init(postCreateEvent.getCreatedRealm());
            }
        });
    }
}
