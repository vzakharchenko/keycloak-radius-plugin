package com.github.vzakharchenko.radius.dm.jpa;

import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModel;

import java.util.List;

public interface DmTableManager {
    void saveRadiusSession(DisconnectMessageModel disconnectMessageModel);

    DisconnectMessageModel getDisconnectMessage(String userName, String radiusSessionId);

    List<DisconnectMessageModel> getAllActiveSessions();

    List<DisconnectMessageModel> getAllActiveSessions(String realmId,
                                                      String calledStationId);

    DisconnectMessageModel getActiveSession(String realmId,
                                            String ip,
                                            String calledStationId);

    void successEndSession(DisconnectMessageModel dmm);

    void successEndSessionWithCause(DisconnectMessageModel dmm, String cause);

    void failEndSession(DisconnectMessageModel dmm, String message);

    void increaseEndAttempts(DisconnectMessageModel dmm, String message);
}
