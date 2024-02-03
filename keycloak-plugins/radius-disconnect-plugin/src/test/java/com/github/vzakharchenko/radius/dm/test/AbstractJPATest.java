package com.github.vzakharchenko.radius.dm.test;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class AbstractJPATest extends AbstractRadiusTest {
    protected JpaConnectionProvider provider;
    @Mock
    protected EntityManager entityManager;

    @Mock
    protected TypedQuery typedQuery;

    @BeforeMethod
    public void beforeJPATests() {
        provider = getProvider(JpaConnectionProvider.class);
        reset(provider);
        reset(entityManager);
        reset(typedQuery);
        when(provider.getEntityManager()).thenReturn(entityManager);
        when(entityManager.createQuery(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());
    }
}
