package com.github.vzakharchenko.radius.dm.jpa;

import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModel;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

public class DisconnectMessageManager implements DmTableManager {

    private final EntityManager em;

    public DisconnectMessageManager(KeycloakSession session) {
        JpaConnectionProvider jpp = session.getProvider(JpaConnectionProvider.class);
        em = jpp.getEntityManager();
    }

    @Override
    public void saveRadiusSession(DisconnectMessageModel disconnectMessageModel) {
        disconnectMessageModel.setCreatedDate(new Date());
        em.persist(disconnectMessageModel);
    }

    @Override
    public DisconnectMessageModel getDisconnectMessage(String userName, String radiusSessionId) {
        TypedQuery<DisconnectMessageModel> query = em.
                createQuery("SELECT dmm FROM DisconnectMessageModel dmm " +
                                "WHERE dmm.userName = :userName " +
                                "and dmm.id = :radiusSessionId " +
                                "and dmm.endDate is null",
                        DisconnectMessageModel.class);
        query.setParameter("userName", userName);
        query.setParameter("radiusSessionId", radiusSessionId);
        List<DisconnectMessageModel> list = query.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public void endSession(DisconnectMessageModel dmm) {
        dmm.setEndDate(new Date());
        em.persist(dmm);
    }
}
