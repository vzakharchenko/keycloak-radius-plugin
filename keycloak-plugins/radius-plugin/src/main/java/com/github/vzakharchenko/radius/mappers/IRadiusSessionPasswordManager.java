package com.github.vzakharchenko.radius.mappers;

import org.keycloak.models.UserSessionModel;
import org.keycloak.representations.IDToken;

public interface IRadiusSessionPasswordManager {
    String getCurrentPassword(UserSessionModel sessionModel);

    String password(UserSessionModel sessionModel, IDToken token);
    void clearIfExists(UserSessionModel sessionModel, String password);
}
