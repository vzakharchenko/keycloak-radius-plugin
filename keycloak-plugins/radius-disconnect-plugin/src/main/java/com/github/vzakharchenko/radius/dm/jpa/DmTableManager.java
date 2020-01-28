package com.github.vzakharchenko.radius.dm.jpa;

import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModel;

import java.util.List;

public interface DmTableManager {
    void saveRadiusSession(DisconnectMessageModel disconnectMessageModel);

    DisconnectMessageModel getDisconnectMessage(String userName, String radiusSessionId);

    List<DisconnectMessageModel> getAllActivedSessions();

    void sucessEndSession(DisconnectMessageModel dmm);

    void failEndSession(DisconnectMessageModel dmm, String message);

    void increaseEndAttempts(DisconnectMessageModel dmm, String message);
}
