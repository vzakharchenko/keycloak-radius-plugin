package ua.zaskarius.keycloak.plugins.radius.radius.server;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import ua.zaskarius.keycloak.plugins.radius.providers.AbstractRadiusServerProviderFactory;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProvider;

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
            if (event instanceof RealmModel.RealmPostCreateEvent) {
                RealmModel.RealmPostCreateEvent postCreateEvent = (RealmModel
                        .RealmPostCreateEvent) event;
                serverProvider.init(postCreateEvent.getCreatedRealm());
            }
        });
    }
}
