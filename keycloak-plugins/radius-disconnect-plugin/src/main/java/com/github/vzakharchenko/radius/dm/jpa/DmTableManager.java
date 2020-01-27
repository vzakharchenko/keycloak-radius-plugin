package com.github.vzakharchenko.radius.dm.jpa;

import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModel;

public interface DmTableManager {
    void saveRadiusSession(DisconnectMessageModel disconnectMessageModel);

    DisconnectMessageModel getDisconnectMessage(String userName, String radiusSessionId);
}
