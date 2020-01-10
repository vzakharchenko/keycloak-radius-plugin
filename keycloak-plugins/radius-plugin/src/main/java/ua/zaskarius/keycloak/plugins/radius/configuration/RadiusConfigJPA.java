package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusConfigModel;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RadiusConfigJPA implements IRadiusConfigJPA {

    private final KeycloakSession session;

    public RadiusConfigJPA(KeycloakSession session) {
        this.session = session;
    }

    private JpaConnectionProvider getJpaConnectionProvider() {
        return session.getProvider(JpaConnectionProvider.class);
    }

    @Override
    public RadiusConfigModel getConfig() {
        EntityManager entityManager = getJpaConnectionProvider().getEntityManager();
        TypedQuery<RadiusConfigModel> query = entityManager
                .createQuery("SELECT radius FROM RadiusConfigModel radius",
                        RadiusConfigModel.class);
        List<RadiusConfigModel> configModels = query.getResultList();
        return configModels == null || configModels.isEmpty() ? null :
                configModels.get(0);
    }

    @Override
    public RadiusConfigModel saveConfig(RadiusConfigModel configModel, UserModel userModel) {
        EntityManager entityManager = getJpaConnectionProvider().getEntityManager();
        RadiusConfigModel config = getConfig();
        if (config != null) {
            configModel.setId(config.getId());
        } else {
            configModel.setId(UUID.randomUUID().toString());
        }
        configModel.setmUserId(userModel.getId());
        configModel.setmDate(new Date());
        entityManager.persist(configModel);
        return configModel;
    }
}
