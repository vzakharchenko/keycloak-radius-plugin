package com.github.vzakharchenko.radius.dm.jpa;

import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModel;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DisconnectMessageManager implements DmTableManager {

    public static final int MAX_ATTEMPTS = 2;
    private final EntityManager em;

    public DisconnectMessageManager(KeycloakSession session) {
        JpaConnectionProvider jpp = session.getProvider(JpaConnectionProvider.class);
        em = jpp.getEntityManager();
    }

    @Override
    public void saveRadiusSession(DisconnectMessageModel disconnectMessageModel) {
        disconnectMessageModel.setCreatedDate(new Date());
        disconnectMessageModel.setModifyDate(new Date());
        disconnectMessageModel.setId(UUID.randomUUID().toString());
        em.persist(disconnectMessageModel);
    }

    @Override
    public DisconnectMessageModel getDisconnectMessage(String userName, String radiusSessionId) {
        TypedQuery<DisconnectMessageModel> query = em.
                createQuery("SELECT dmm FROM DisconnectMessageModel dmm " +
                                "WHERE dmm.userName = :userName " +
                                "and dmm.radiusSessionId = :radiusSessionId " +
                                "and dmm.endDate is null",
                        DisconnectMessageModel.class);
        query.setParameter("userName", userName);
        query.setParameter("radiusSessionId", radiusSessionId);
        List<DisconnectMessageModel> list = query.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<DisconnectMessageModel> getAllActivedSessions() {
        TypedQuery<DisconnectMessageModel> query = em.
                createQuery("SELECT dmm FROM DisconnectMessageModel dmm " +
                                "where dmm.endDate is null",
                        DisconnectMessageModel.class);
        return query.getResultList();
    }

    @Override
    public void sucessEndSession(DisconnectMessageModel dmm) {
        dmm.setEndDate(new Date());
        dmm.setModifyDate(new Date());
        dmm.setEndStatus("ACK");
        dmm.setEndMessage("Disconnected");
        em.persist(dmm);
    }

    @Override
    public void failEndSession(DisconnectMessageModel dmm, String message) {
        dmm.setEndDate(new Date());
        dmm.setModifyDate(new Date());
        dmm.setEndStatus("NAK");
        dmm.setEndMessage(message);
        em.persist(dmm);
    }

    @Override
    public void increaseEndAttempts(DisconnectMessageModel dmm, String message) {
        int attempts = dmm.getAttempts() == null ? 0 : dmm.getAttempts();
        if (attempts > MAX_ATTEMPTS) {
            dmm.setEndDate(new Date());
            dmm.setModifyDate(new Date());
            dmm.setEndStatus("MAX_ATTEMPTS");
            dmm.setEndMessage(message);
        } else {
            dmm.setAttempts(attempts + 1);
        }
        em.persist(dmm);
    }

}
