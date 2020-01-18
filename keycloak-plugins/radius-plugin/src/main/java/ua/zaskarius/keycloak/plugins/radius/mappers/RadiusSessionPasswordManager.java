package ua.zaskarius.keycloak.plugins.radius.mappers;

import org.keycloak.models.UserSessionModel;
import ua.zaskarius.keycloak.plugins.radius.RadiusHelper;

import static ua.zaskarius.keycloak.plugins.radius.mappers.RadiusPasswordMapper.RADIUS_SESSION_PASSWORD;

public final class RadiusSessionPasswordManager implements IRadiusSessionPasswordManager {

    private static final RadiusSessionPasswordManager
            INSTANCE = new RadiusSessionPasswordManager();

    private RadiusSessionPasswordManager() {
    }

    public static RadiusSessionPasswordManager getInstance() {
        return INSTANCE;
    }

    @Override
    public String password(UserSessionModel sessionModel) {
        String sessionNote = sessionModel.getNote(RADIUS_SESSION_PASSWORD);
        if (sessionNote == null) {
            sessionNote = RadiusHelper.generatePassword();
            sessionModel.setNote(RADIUS_SESSION_PASSWORD, sessionNote);
        }
        return sessionNote;
    }
}
