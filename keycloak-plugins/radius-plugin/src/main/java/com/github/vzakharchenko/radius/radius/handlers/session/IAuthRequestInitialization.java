package com.github.vzakharchenko.radius.radius.handlers.session;

import com.github.vzakharchenko.radius.radius.handlers.protocols.AuthProtocol;
import org.keycloak.models.KeycloakSession;

import java.net.InetSocketAddress;

public interface IAuthRequestInitialization {
    boolean init(InetSocketAddress address,
                 String username,
                 AuthProtocol protocol,
                 KeycloakSession threadSession);

    void afterAuth(int action,
                   KeycloakSession threadSession);
}
