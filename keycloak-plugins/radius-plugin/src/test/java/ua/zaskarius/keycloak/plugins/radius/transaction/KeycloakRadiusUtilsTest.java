package ua.zaskarius.keycloak.plugins.radius.transaction;

import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.keycloak.models.KeycloakSession;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class KeycloakRadiusUtilsTest extends AbstractRadiusTest {

    @Test
    public void testActiveTran() {
        Boolean res = KeycloakRadiusUtils.runJobInTransaction(keycloakSessionFactory, session -> true);
        assertTrue(res);
        verify(keycloakTransactionManager).commit();
    }

    @Test
    public void testActiveTran2() {
        Boolean res = KeycloakRadiusUtils.runJobInTransaction(session, session -> true);
        assertTrue(res);
        verify(keycloakTransactionManager).commit();
    }

    @Test
    public void testNotActiveTran() {
        when(keycloakTransactionManager.isActive()).thenReturn(false);
        Boolean res = KeycloakRadiusUtils.runJobInTransaction(keycloakSessionFactory,
                session -> true);
        assertNull(res);
        verify(keycloakTransactionManager, never()).commit();
        verify(keycloakTransactionManager, never()).rollback();
    }

    @Test
    public void testRollBackOnlyTran() {
        when(keycloakTransactionManager.getRollbackOnly()).thenReturn(true);
        Boolean res = KeycloakRadiusUtils.runJobInTransaction(keycloakSessionFactory,
                session -> true);
        assertNull(res);
        verify(keycloakTransactionManager).rollback();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testException() {
        //when(keycloakTransactionManager.getRollbackOnly()).thenReturn(true);
        Boolean res = KeycloakRadiusUtils.runJobInTransaction(keycloakSessionFactory,
                new KeycloakSessionTaskWithReturn<Boolean>() {
                    @Override
                    public Boolean run(KeycloakSession session) {
                        throw new IllegalStateException("test");
                    }
                });
        assertNull(res);
        verify(keycloakTransactionManager,never()).rollback();
        verify(keycloakTransactionManager).rollback();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testExceptionNotActive() {
        when(keycloakTransactionManager.isActive()).thenReturn(false);
        Boolean res = KeycloakRadiusUtils.runJobInTransaction(keycloakSessionFactory,
                session -> {
                    throw new IllegalStateException("test");
                });
        assertNull(res);
        verify(keycloakTransactionManager,never()).rollback();
    }
}
