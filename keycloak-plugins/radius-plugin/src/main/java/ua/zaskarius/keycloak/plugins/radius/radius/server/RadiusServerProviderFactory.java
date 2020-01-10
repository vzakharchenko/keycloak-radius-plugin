package ua.zaskarius.keycloak.plugins.radius.radius.server;

import com.google.common.annotations.VisibleForTesting;
import ua.zaskarius.keycloak.plugins.radius.configuration.ConfigurationScheduledTask;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.scheduled.ClusterAwareScheduledTaskRunner;
import org.keycloak.timer.TimerProvider;

public class RadiusServerProviderFactory
        implements IRadiusServerProviderFactory<IRadiusServerProvider> {

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
        KeycloakSession session = factory.create();
        session.getTransactionManager().begin();
        try {
            create(session);
            TimerProvider timer = session.getProvider(TimerProvider.class);
            timer.schedule(new ClusterAwareScheduledTaskRunner(
                            factory,
                            ConfigurationScheduledTask.getInstance(),
                            60_000),
                    60_000,
                    "initial Radius");
        } finally {
            session.getTransactionManager().commit();
        }
    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "radius-provider";
    }

    @VisibleForTesting
    void setMikrotikRadiusServer(IRadiusServerProvider mikrotikRadiusServer) {
        this.mikrotikRadiusServer = mikrotikRadiusServer;
    }
}
