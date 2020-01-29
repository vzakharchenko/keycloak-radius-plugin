package com.github.vzakharchenko.radius.dm.jpa;

import com.github.vzakharchenko.radius.dm.models.DMClientEndModel;
import com.github.vzakharchenko.radius.dm.models.DMKeycloakEndModel;
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
                                "and dmm.radiusSessionId = :radiusSessionId ",
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
                                " left join DMKeycloakEndModel kem on (dmm.id=kem.id)" +
                                " left join DMClientEndModel cem on (dmm.id=cem.id) " +
                                "WHERE kem.endDate is null and cem.endDate is null",
                        DisconnectMessageModel.class);
        return query.getResultList();
    }

    @Override
    public void successEndSession(DisconnectMessageModel dmm) {
        DMKeycloakEndModel kem = new DMKeycloakEndModel();
        kem.setEndDate(new Date());
        kem.setId(dmm.getId());
        kem.setModifyDate(new Date());
        kem.setEndStatus("ACK");
        kem.setEndMessage("Disconnected");
        em.persist(kem);
    }

    @Override
    public void successEndSessionWithCause(DisconnectMessageModel dmm, String cause) {
        DMClientEndModel cem = getDMClientEndModel(dmm, DMClientEndModel.class);
        if (cem == null) {
            cem = new DMClientEndModel();
        }
        cem.setEndDate(new Date());
        cem.setId(dmm.getId());
        cem.setModifyDate(new Date());
        cem.setEndCause(cause);
        em.persist(cem);
    }

    @Override
    public void failEndSession(DisconnectMessageModel dmm, String message) {
        DMKeycloakEndModel kem = getDMClientEndModel(dmm, DMKeycloakEndModel.class);
        if (kem == null) {
            kem = new DMKeycloakEndModel();
        }
        kem.setEndDate(new Date());
        kem.setId(dmm.getId());
        kem.setModifyDate(new Date());
        kem.setEndStatus("NAK");
        kem.setEndMessage(message);
        em.persist(kem);
    }

    protected <T> T getDMClientEndModel(DisconnectMessageModel dmm, Class<T> m) {
        TypedQuery<T> query = em.
                createQuery("SELECT m FROM " + m.getCanonicalName() + " m " +
                                "WHERE m.id = :id ",
                        m);
        query.setParameter("id", dmm.getId());
        List<T> list = query.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    private void increaseEndAttempts(DMKeycloakEndModel kem, String message) {
        Integer attempts = kem.getAttempts();
        if (attempts > MAX_ATTEMPTS) {
            kem.setEndDate(new Date());
            kem.setModifyDate(new Date());
            kem.setEndStatus("MAX_ATTEMPTS");
            kem.setEndMessage(message);
        } else {
            kem.setAttempts(attempts + 1);
        }
    }

    @Override
    public void increaseEndAttempts(DisconnectMessageModel dmm, String message) {
        DMKeycloakEndModel kem = getDMClientEndModel(dmm, DMKeycloakEndModel.class);
        if (kem == null) {
            kem = new DMKeycloakEndModel();
            kem.setId(dmm.getId());
            kem.setAttempts(0);
        }
        increaseEndAttempts(kem, message);
        em.persist(kem);
    }

}
