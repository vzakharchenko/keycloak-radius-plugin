package com.github.vzakharchenko.radius.radius.handlers.otp;

import com.github.vzakharchenko.radius.models.OtpHolder;
import org.keycloak.models.KeycloakSession;

import java.util.Map;

public interface IOtpPasswordFactory {
    Map<String, OtpHolder> getOTPs(KeycloakSession session);

    void validOTP(KeycloakSession session, String password, String credId, String type);
}
