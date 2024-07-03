package com.github.vzakharchenko.radius.mappers;

import com.github.vzakharchenko.radius.RadiusHelper;
import org.apache.commons.lang3.BooleanUtils;
import org.keycloak.common.util.Time;
import org.keycloak.models.UserSessionModel;
import org.keycloak.representations.IDToken;

import java.util.Objects;


public final class RadiusSessionPasswordManager implements IRadiusSessionPasswordManager {


    public static final String RADIUS_SESSION_PASSWORD = "RADIUS_SESSION_PASSWORD";
    public static final String RADIUS_SESSION_EXPIRATION = "RADIUS_SESSION_EXPIRATION";
    public static final String RADIUS_SESSION_PASSWORD_TYPE = "RADIUS_SESSION_PASSWORD_TYPE";

    private static final RadiusSessionPasswordManager
            INSTANCE = new RadiusSessionPasswordManager();

    private RadiusSessionPasswordManager() {
    }

    public static IRadiusSessionPasswordManager getInstance() {
        return INSTANCE;
    }

    @Override
    public String getCurrentPassword(UserSessionModel sessionModel) {
        String password = sessionModel.getNote(RADIUS_SESSION_PASSWORD);
        String expiration = sessionModel.getNote(RADIUS_SESSION_EXPIRATION);
        if (expiration != null &&
                Long.parseLong(expiration) > Time.currentTime()) {
            return password;
        } else {
            clear(sessionModel);
            return null;
        }
    }

    @Override
    public String password(UserSessionModel sessionModel, IDToken token, Boolean oneTomePassword) {
        String sessionNote = getCurrentPassword(sessionModel);
        if (sessionNote == null) {
            sessionNote = RadiusHelper.generatePassword();
            sessionModel.setNote(RADIUS_SESSION_PASSWORD, sessionNote);
            sessionModel.setNote(RADIUS_SESSION_EXPIRATION, String.valueOf(token.getExp()));
            sessionModel.setNote(RADIUS_SESSION_PASSWORD_TYPE, String.valueOf(BooleanUtils
                    .toBooleanDefaultIfNull(oneTomePassword, true)));
        }
        return sessionNote;
    }

    private void clear(UserSessionModel sessionModel) {
        sessionModel.removeNote(RADIUS_SESSION_PASSWORD);
        sessionModel.removeNote(RADIUS_SESSION_EXPIRATION);
    }

    @Override
    public void clearIfExists(UserSessionModel sessionModel, String password) {
        String sessionNote = getCurrentPassword(sessionModel);
        if (Objects.equals(sessionNote, password) &&
                BooleanUtils.toBooleanDefaultIfNull(
                        BooleanUtils.toBooleanObject(sessionModel
                                .getNote(RADIUS_SESSION_PASSWORD_TYPE)), true)) {
            clear(sessionModel);
        }
    }
}
