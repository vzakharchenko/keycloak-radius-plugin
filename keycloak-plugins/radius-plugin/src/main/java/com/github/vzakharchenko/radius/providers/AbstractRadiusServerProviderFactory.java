package com.github.vzakharchenko.radius.providers;

import com.google.common.annotations.VisibleForTesting;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.utils.KeycloakModelUtils;

public abstract class AbstractRadiusServerProviderFactory
        implements IRadiusServerProviderFactory<IRadiusServerProvider> {

    private IRadiusServerProvider radiusServerProvider;

    protected abstract IRadiusServerProvider createInstance(KeycloakSession session);

    @Override
    public IRadiusServerProvider create(KeycloakSession session) {
        if (radiusServerProvider == null) {
            radiusServerProvider = createInstance(session);
        }
        return radiusServerProvider;
    }

    @Override
    public void init(Config.Scope config) {

    }


    @Override
    public void close() {

    }

    public abstract void postInit(KeycloakSession session,
                                  IRadiusServerProvider serverProvider);

    @Override
    public final void postInit(KeycloakSessionFactory factory) {
        KeycloakModelUtils.runJobInTransaction(factory, session ->
                postInit(session, create(session)));
    }

    @VisibleForTesting
    public void setRadiusServer(IRadiusServerProvider radiusServer) {
        this.radiusServerProvider = radiusServer;
    }
}
