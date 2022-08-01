package com.github.vzakharchenko.radius.dm.logout;

import org.jboss.logging.Logger;
import org.keycloak.cluster.ClusterProvider;
import org.keycloak.cluster.ExecutionResult;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.scheduled.ScheduledTaskRunner;
import org.keycloak.timer.ScheduledTask;

@Deprecated
public class RadiusClusterAwareScheduledTaskRunner extends ScheduledTaskRunner {
    private static final Logger logger = Logger.getLogger(org.keycloak.services.scheduled.ClusterAwareScheduledTaskRunner.class);
    private final int intervalSecs;

    public RadiusClusterAwareScheduledTaskRunner(KeycloakSessionFactory sessionFactory, ScheduledTask task, long intervalMillis) {
        super(sessionFactory, task);
        this.intervalSecs = (int) (intervalMillis / 1000L);
    }

    protected void runTask(final KeycloakSession session) {
        session.getTransactionManager().begin();
        ClusterProvider clusterProvider = (ClusterProvider) session.getProvider(ClusterProvider.class);
        ScheduledTask scheduledTask = this.task;
        String taskKey = scheduledTask.getClass().getSimpleName();
        ExecutionResult<Void> result = clusterProvider.executeIfNotExecuted(taskKey, this.intervalSecs, () -> {
            scheduledTask.run(session);
            return null;
        });
        session.getTransactionManager().commit();
        if (result.isExecuted()) {
            logger.debugf("Executed scheduled task %s", taskKey);
        } else {
            logger.debugf("Skipped execution of task %s as other cluster node is executing it", taskKey);
        }

    }
}

