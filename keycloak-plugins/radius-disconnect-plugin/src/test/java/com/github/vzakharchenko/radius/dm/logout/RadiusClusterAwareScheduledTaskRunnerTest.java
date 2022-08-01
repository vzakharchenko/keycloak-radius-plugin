package com.github.vzakharchenko.radius.dm.logout;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.keycloak.cluster.ClusterProvider;
import org.keycloak.cluster.ExecutionResult;
import org.keycloak.models.KeycloakSession;
import org.keycloak.timer.ScheduledTask;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.Callable;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RadiusClusterAwareScheduledTaskRunnerTest extends AbstractRadiusTest {
    private ClusterProvider provider;

    @BeforeMethod
    public void beforeMethod() {
        provider = session.getProvider(ClusterProvider.class);
        reset(provider);
        when(provider.executeIfNotExecuted(anyString(), anyInt(),
                any())).thenReturn(ExecutionResult.executed("executed"));
    }


    @Test
    public void testClusterEvent() throws InterruptedException {
        RadiusClusterAwareScheduledTaskRunner clusterAwareScheduledTaskRunner =
                new RadiusClusterAwareScheduledTaskRunner(
                        session.getKeycloakSessionFactory(), new ScheduledTask() {
                    @Override
                    public void run(KeycloakSession session) {
                    }
                }, 3000);
        clusterAwareScheduledTaskRunner.run();
        verify(provider).executeIfNotExecuted(anyString(), anyInt(), any());
    }

    @Test
    public void testClusterEventNotExecuted() throws InterruptedException {
        reset(provider);
        when(provider.executeIfNotExecuted(anyString(), anyInt(), any()))
                .thenReturn(ExecutionResult.notExecuted());
        RadiusClusterAwareScheduledTaskRunner clusterAwareScheduledTaskRunner =
                new RadiusClusterAwareScheduledTaskRunner(
                        session.getKeycloakSessionFactory(), new ScheduledTask() {
                    @Override
                    public void run(KeycloakSession session) {
                    }
                }, 3000);
        clusterAwareScheduledTaskRunner.run();
        verify(provider).executeIfNotExecuted(anyString(), anyInt(), any());
    }

    @Test
    public void testClusterEventNotExecuted2() throws InterruptedException {
        reset(provider);

        when(provider.executeIfNotExecuted(anyString(), anyInt(), any()))
                .then(invocationOnMock -> {
                    Callable argument = invocationOnMock.getArgument(2, Callable.class);
                    argument.call();
                    return ExecutionResult.executed("test");
                });
        RadiusClusterAwareScheduledTaskRunner clusterAwareScheduledTaskRunner =
                new RadiusClusterAwareScheduledTaskRunner(
                        session.getKeycloakSessionFactory(), new ScheduledTask() {
                    @Override
                    public void run(KeycloakSession session) {
                    }
                }, 3000);
        clusterAwareScheduledTaskRunner.run();
        verify(provider).executeIfNotExecuted(
                anyString(), anyInt(), any());
    }
}
