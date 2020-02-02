package com.github.vzakharchenko.radius.mappers;

import com.github.vzakharchenko.radius.RadiusHelper;
import org.keycloak.models.UserSessionModel;

import java.util.Objects;

import static com.github.vzakharchenko.radius.mappers.RadiusPasswordMapper.RADIUS_SESSION_PASSWORD;

public final class RadiusSessionPasswordManager implements IRadiusSessionPasswordManager {

    private static final RadiusSessionPasswordManager
            INSTANCE = new RadiusSessionPasswordManager();

    private RadiusSessionPasswordManager() {
    }

    public static RadiusSessionPasswordManager getInstance() {
        return INSTANCE;
    }

    private String getSessionNote(UserSessionModel sessionModel) {
        return sessionModel.getNote(RADIUS_SESSION_PASSWORD);
    }

    @Override
    public String password(UserSessionModel sessionModel) {
        String sessionNote = getSessionNote(sessionModel);
        if (sessionNote == null) {
            sessionNote = RadiusHelper.generatePassword();
            sessionModel.setNote(RADIUS_SESSION_PASSWORD, sessionNote);
        }
        return sessionNote;
    }

    @Override
    public void clearIfExists(UserSessionModel sessionModel, String password) {
        String sessionNote = getSessionNote(sessionModel);
        if (sessionNote != null && Objects.equals(sessionNote, password)) {
            sessionModel.removeNote(RADIUS_SESSION_PASSWORD);
        }
    }
}
