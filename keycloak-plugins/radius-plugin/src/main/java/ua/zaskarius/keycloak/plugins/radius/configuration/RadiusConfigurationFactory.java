package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.keycloak.Config;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusConfigModel;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusParameterModel;

import java.util.Arrays;
import java.util.List;

public class RadiusConfigurationFactory
        implements JpaEntityProvider, JpaEntityProviderFactory {

    public static final String RADIUS_JTA_FACTORY = "radius-jta-factory";
    public static final String RADIUS_JPA = "radius-JPA";

    @Override
    public List<Class<?>> getEntities() {
        return Arrays.asList(
                RadiusConfigModel.class,
                RadiusParameterModel.class);
    }

    @Override
    public String getChangelogLocation() {
        return "radius-changelog.xml";
    }

    @Override
    public String getFactoryId() {
        return RADIUS_JTA_FACTORY;
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
        return RADIUS_JPA;
    }
}
