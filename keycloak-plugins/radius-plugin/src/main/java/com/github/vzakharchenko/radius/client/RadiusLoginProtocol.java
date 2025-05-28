package com.github.vzakharchenko.radius.client;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.jaxrs.ResponseBuilderImpl;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.*;
import org.keycloak.protocol.ClientData;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.services.ErrorResponseException;
import org.keycloak.sessions.AuthenticationSessionModel;

public class RadiusLoginProtocol implements LoginProtocol {
    private static final Logger LOGGER = Logger.getLogger(RadiusLoginProtocol.class);

    @Override
    public LoginProtocol setSession(KeycloakSession keycloakSession) {
        // not used: this.session = keycloakSession;
        return this;
    }

    @Override
    public LoginProtocol setRealm(RealmModel realmModel) {
        // not used: this.realm = realmModel;
        return this;
    }

    @Override
    public LoginProtocol setUriInfo(UriInfo info) {
     throw new UnsupportedOperationException("RadiusLoginProtocol does not support UriInfo");
    }

    @Override
    public LoginProtocol setHttpHeaders(HttpHeaders httpHeaders) {
        throw new UnsupportedOperationException("RadiusLoginProtocol does not support HttpHeaders");
    }

    @Override
    public LoginProtocol setEventBuilder(EventBuilder builder) {
        // not used: this.eventBuilder = builder;
        return this;
    }

    @Override
    public Response authenticated(AuthenticationSessionModel authSession,
                                  UserSessionModel userSession,
                                  ClientSessionContext clientSessionCtx) {
        return new ResponseBuilderImpl().status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public Response sendError(AuthenticationSessionModel authSession, Error error,
                              String errorMessage) {
        return new ResponseBuilderImpl().status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public Response sendError(ClientModel client, ClientData clientData, Error error) {
        return new ResponseBuilderImpl().status(Response.Status.INTERNAL_SERVER_ERROR).build();

    }

    @Override
    public ClientData getClientData(AuthenticationSessionModel authSession) {
        return new ClientData();
    }

    @Override
    public Response backchannelLogout(UserSessionModel userSession,
                                      AuthenticatedClientSessionModel clientSession) {
        return errorResponse(userSession, "backchannelLogout");
    }

    @Override
    public Response frontchannelLogout(UserSessionModel userSession,
                                       AuthenticatedClientSessionModel clientSession) {
        return errorResponse(userSession, "frontchannelLogout");
    }

    @Override
    public Response finishBrowserLogout(UserSessionModel userSession,
                                        AuthenticationSessionModel logoutSession) {
        return errorResponse(userSession, "finishLogout");

    }

    @Override
    public boolean requireReauthentication(UserSessionModel userSession,
                                           AuthenticationSessionModel authSession) {
        return true;
    }

    @Override
    public void close() {
        // nothing to do
    }

    private Response errorResponse(final UserSessionModel userSession, final String methodName) {
        LOGGER.errorv("User {0} attempted to invoke unsupported method {1} on RADIUS protocol.",
                userSession.getUser().getUsername(), methodName);
        throw new ErrorResponseException("invalid_request",
                "Attempted to invoke unsupported RADIUS protocol method %s".formatted(methodName),
                Response.Status.BAD_REQUEST);
    }
}
