package ua.zaskarius.keycloak.plugins.radius.mappers;

import org.keycloak.models.UserSessionModel;

public interface IRadiusSessionPasswordManager {
    String password(UserSessionModel sessionModel);
}
