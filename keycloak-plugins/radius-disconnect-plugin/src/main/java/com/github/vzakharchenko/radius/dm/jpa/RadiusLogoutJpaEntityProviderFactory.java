package com.github.vzakharchenko.radius.dm.jpa;

import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModel;
import org.keycloak.Config;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.util.Collections;
import java.util.List;

public class RadiusLogoutJpaEntityProviderFactory implements JpaEntityProviderFactory,
        JpaEntityProvider {

    public static final String RADIUS_DISCONNECT_MESSAGE_FACTORY =
            "radius-disconnect-message-factory";
    public static final String RADIUS_DM = "radius-dm";

    @Override
    public List<Class<?>> getEntities() {
        return Collections.singletonList(DisconnectMessageModel.class);
    }

    @Override
    public String getChangelogLocation() {
        return "dm-changelog.xml";
    }

    @Override
    public String getFactoryId() {
        return RADIUS_DM;
    }

    @Override
    public JpaEntityProvider create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return RADIUS_DISCONNECT_MESSAGE_FACTORY;
    }
}
