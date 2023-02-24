package com.github.vzakharchenko.radius.dm.logout;

import org.jboss.logging.Logger;
import org.keycloak.cluster.ClusterProvider;
import org.keycloak.cluster.ExecutionResult;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.KeycloakTransactionManager;
import org.keycloak.services.scheduled.ClusterAwareScheduledTaskRunner;
import org.keycloak.services.scheduled.ScheduledTaskRunner;
import org.keycloak.timer.ScheduledTask;

@Deprecated
public class RadiusClusterAwareScheduledTaskRunner extends ScheduledTaskRunner {
    private static final Logger LOGGER = Logger
            .getLogger(ClusterAwareScheduledTaskRunner.class);
    private final int intervalSecs;

    public RadiusClusterAwareScheduledTaskRunner(KeycloakSessionFactory sessionFactory,
                                                 ScheduledTask task, long intervalMillis) {
        super(sessionFactory, task);
        this.intervalSecs = (int) (intervalMillis / 1000L);
    }

    @Override
    protected void runTask(final KeycloakSession session) {
        KeycloakTransactionManager transactionManager = session.getTransactionManager();

        boolean activeTransaction = transactionManager.isActive();
        if (!activeTransaction){
            transactionManager.begin();
        }
        ClusterProvider clusterProvider = session.getProvider(ClusterProvider.class);
        ScheduledTask scheduledTask = this.task;
        String taskKey = scheduledTask.getClass().getSimpleName();
        ExecutionResult<Void> result = clusterProvider
                .executeIfNotExecuted(taskKey, this.intervalSecs, () -> {
                    scheduledTask.run(session);
                    return null;
                });
        if (!activeTransaction){
            transactionManager.commit();
        }
        if (result.isExecuted()) {
            LOGGER.debugf("Executed scheduled task %s", taskKey);
        } else {
            LOGGER.debugf("Skipped execution of task %s as other cluster node is executing it",
                    taskKey);
        }

    }
}

