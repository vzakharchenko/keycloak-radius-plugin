package com.github.vzakharchenko.radius.mappers;

import com.github.vzakharchenko.radius.RadiusHelper;
import org.keycloak.models.UserSessionModel;

import static com.github.vzakharchenko.radius.mappers.RadiusPasswordMapper.RADIUS_SESSION_PASSWORD;

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
