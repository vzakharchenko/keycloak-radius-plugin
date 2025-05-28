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

    private void runInTransaction(InTransaction inTransaction,
                                  KeycloakSession session,
                                  ScheduledTask scheduledTask) {
        KeycloakTransactionManager transactionManager = session.getTransactionManager();

        boolean activeTransaction = transactionManager.isActive();
        if (!activeTransaction) {
            transactionManager.begin();
        }
        inTransaction.run(scheduledTask);
        if (!activeTransaction) {
            transactionManager.commit();
        }
    }

    @Override
    protected void runTask(final KeycloakSession session) {
        this.runInTransaction(scheduledTask -> {
            ClusterProvider clusterProvider = session.getProvider(ClusterProvider.class);
            String taskKey = scheduledTask.getClass().getSimpleName();
            ExecutionResult<Void> result = clusterProvider
                    .executeIfNotExecuted(taskKey, intervalSecs, () -> {
                        scheduledTask.run(session);
                        return null;
                    });
            if (result.isExecuted()) {
                LOGGER.debugf("Executed scheduled task %s", taskKey);
            } else {
                LOGGER.debugf("Skipped execution of task %s as other cluster node is executing it",
                        taskKey);
            }

        }, session, this.task);
    }

    private interface InTransaction {
        void run(ScheduledTask scheduledTask);
    }
}

