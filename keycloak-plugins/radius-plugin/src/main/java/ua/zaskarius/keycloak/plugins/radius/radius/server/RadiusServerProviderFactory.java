package ua.zaskarius.keycloak.plugins.radius.radius.server;

import com.google.common.annotations.VisibleForTesting;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProviderFactory;

public class RadiusServerProviderFactory
        implements IRadiusServerProviderFactory<IRadiusServerProvider> {

    public static final String RADIUS_PROVIDER = "radius-provider";
    private IRadiusServerProvider mikrotikRadiusServer;

    @Override
    public IRadiusServerProvider create(KeycloakSession session) {
        if (mikrotikRadiusServer == null) {
            mikrotikRadiusServer = new KeycloakRadiusServer(session);
        }
        return mikrotikRadiusServer;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        factory.register(event -> {
            if (event instanceof RealmModel.RealmPostCreateEvent) {
                RealmModel.RealmPostCreateEvent postCreateEvent = (RealmModel
                        .RealmPostCreateEvent) event;
                KeycloakSession keycloakSession = postCreateEvent
                        .getKeycloakSession();
                IRadiusServerProvider iRadiusServerProvider = create(keycloakSession);
                iRadiusServerProvider.init(postCreateEvent.getCreatedRealm());
            }
        });
        KeycloakModelUtils.runJobInTransaction(factory, this::create);
    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return RADIUS_PROVIDER;
    }

    @VisibleForTesting
    void setMikrotikRadiusServer(IRadiusServerProvider mikrotikRadiusServer) {
        this.mikrotikRadiusServer = mikrotikRadiusServer;
    }
}
