package com.github.vzakharchenko.radius.radius.handlers.otp;

import org.keycloak.models.KeycloakSession;

public interface IOtpPasswordFactory {
    OtpPasswordInfo getOTPs(KeycloakSession session);

    void validOTP(KeycloakSession session, String password, String credId, String type);
}
