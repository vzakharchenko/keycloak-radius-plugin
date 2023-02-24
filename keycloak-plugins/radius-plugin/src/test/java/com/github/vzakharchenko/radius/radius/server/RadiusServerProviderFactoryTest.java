package com.github.vzakharchenko.radius.radius.server;

import com.github.vzakharchenko.radius.providers.IRadiusServerProvider;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

public class RadiusServerProviderFactoryTest extends AbstractRadiusTest {
    private final RadiusServerProviderFactory radiusServerProviderFactory = new RadiusServerProviderFactory();

    @Mock
    private IRadiusServerProvider mikrotikRadiusServer;

    @BeforeMethod
    public void beforeMethod() {
        radiusServerProviderFactory.setRadiusServer(mikrotikRadiusServer);
        doNothing().when(keycloakSessionFactory)
                .register(argThat(argument -> {
                    RealmModel.RealmPostCreateEvent postCreateEvent = new RealmModel.RealmPostCreateEvent() {
                        @Override
                        public RealmModel getCreatedRealm() {
                            return realmModel;
                        }

                        @Override
                        public KeycloakSession getKeycloakSession() {
                            return session;
                        }
                    };
                    argument.onEvent(postCreateEvent);
                    return true;
                }));
    }

    @Test
    public void testMethods() {
        radiusServerProviderFactory.close();
        radiusServerProviderFactory.create(session);
        radiusServerProviderFactory.init(null);
        assertEquals(radiusServerProviderFactory.getId(), "radius-provider");
    }


    @Test
    public void testPostInit() {
        radiusServerProviderFactory.postInit(keycloakSessionFactory);
        verify(keycloakTransactionManager).begin();
    }

    @Test
    public void testPostEvent() {
        doNothing().when(keycloakSessionFactory)
                .register(argThat(argument -> {
                    RealmModel.RealmCreationEvent postCreateEvent = new RealmModel.RealmCreationEvent() {
                        @Override
                        public RealmModel getCreatedRealm() {
                            return realmModel;
                        }

                        @Override
                        public KeycloakSession getKeycloakSession() {
                            return session;
                        }
                    };
                    argument.onEvent(postCreateEvent);
                    return true;
                }));
        radiusServerProviderFactory.postInit(keycloakSessionFactory);
        verify(keycloakTransactionManager).begin();
//        verify(keycloakTransactionManager).commit();
    }


    @Override
    protected List<? extends Object> resetMock() {
        return Arrays.asList(mikrotikRadiusServer);
    }
}
