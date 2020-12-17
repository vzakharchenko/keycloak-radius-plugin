package com.github.vzakharchenko.radius.client;

import org.keycloak.events.EventBuilder;
import org.keycloak.models.*;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.sessions.AuthenticationSessionModel;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public class RadiusLoginProtocol implements LoginProtocol {


    @Override
    public LoginProtocol setSession(KeycloakSession session) {
        return this;
    }

    @Override
    public LoginProtocol setRealm(RealmModel realm) {
        return this;
    }

    @Override
    public LoginProtocol setUriInfo(UriInfo uriInfo) {
        return this;
    }

    @Override
    public LoginProtocol setHttpHeaders(HttpHeaders headers) {
        return this;
    }

    @Override
    public LoginProtocol setEventBuilder(EventBuilder event) {
        return this;
    }

    @Override
    public Response authenticated(AuthenticationSessionModel authSession,
                                  UserSessionModel userSession,
                                  ClientSessionContext clientSessionCtx) {
        return null;
    }

    @Override
    public Response sendError(AuthenticationSessionModel authSession,
                              Error error) {
        return null;
    }

    @Override
    public Response backchannelLogout(UserSessionModel userSession,
                                      AuthenticatedClientSessionModel clientSession) {
        return null;
    }

    @Override
    public Response frontchannelLogout(UserSessionModel userSession,
                                       AuthenticatedClientSessionModel clientSession) {
        return null;
    }

    @Override
    public Response finishLogout(UserSessionModel userSession) {
        return null;
    }

    @Override
    public boolean requireReauthentication(UserSessionModel userSession,
                                           AuthenticationSessionModel authSession) {
        return false;
    }

    @Override
    public void close() {

    }
}
