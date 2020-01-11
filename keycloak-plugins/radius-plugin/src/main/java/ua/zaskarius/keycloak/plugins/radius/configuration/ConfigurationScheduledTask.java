package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.timer.ScheduledTask;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProviderFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ConfigurationScheduledTask implements ScheduledTask {

    private static final Logger LOGGER = Logger.getLogger(ConfigurationScheduledTask.class);

    private static final ConfigurationScheduledTask INSTANCE = new ConfigurationScheduledTask();

    public Map<Class<? extends IRadiusServerProviderFactory>,
            IRadiusServerProviderFactory<? extends IRadiusServerProvider>>
            connectionProviderMap = new HashMap<>();

    private ConfigurationScheduledTask() {
    }

    public static void addConnectionProviderMap(
            IRadiusServerProviderFactory<? extends IRadiusServerProvider>
                    connectionProvider) {
        INSTANCE.connectionProviderMap.put(connectionProvider.getClass(), connectionProvider);
    }

    public static ScheduledTask getInstance() {
        return INSTANCE;
    }


    @Override
    public void run(KeycloakSession session) {
        LOGGER.info("Start Radius configuration job ");
        List<RealmModel> realms = session.realms().getRealms();
        for (RealmModel realm : realms) {
            for (IRadiusServerProviderFactory<? extends IRadiusServerProvider>
                    connectionProvider : connectionProviderMap.values()) {
                IRadiusServerProvider provider = connectionProvider.create(session);
                try {
                    boolean changed = provider.init(realm);
                    if (changed) {
                        LOGGER.info(" Radius provider initialization for Realm " + realm.getName()
                                + " executed ");
                    }
                } catch (Exception e) {
                    LOGGER.warn("skip Exception", e);
                    LOGGER.info(" Radius provider initialization fail for Realm " + realm.getName()
                            + " executed ");
                }
            }
        }
        LOGGER.info(" Radius configuration job successfully completed ");
    }
}
