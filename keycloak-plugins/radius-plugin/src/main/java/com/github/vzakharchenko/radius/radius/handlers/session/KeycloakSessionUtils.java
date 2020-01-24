package com.github.vzakharchenko.radius.radius.handlers.session;

import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfo;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoGetter;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.keycloak.common.ClientConnection;
import org.keycloak.common.util.Resteasy;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakTransaction;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;


public final class KeycloakSessionUtils {

    private static final String RADIUS_INFO_ATTRIBUTE = "RADIUS_INFO";

    private KeycloakSessionUtils() {
    }

    public static <T> T getSessionAttribute(KeycloakSession session, String name, Class<T> tClass) {
        return session.getAttribute(name, tClass);
    }

    public static IRadiusUserInfoGetter getRadiusUserInfo(KeycloakSession session) {
        return getSessionAttribute(session, RADIUS_INFO_ATTRIBUTE, IRadiusUserInfoGetter.class);
    }

    public static void addAttribute(KeycloakSession session, String name, Object value) {
        session.setAttribute(name, value);
    }

    public static void addRadiusUserInfo(KeycloakSession session,
                                         IRadiusUserInfoGetter radiusUserInfoGetter) {
        addAttribute(session, RADIUS_INFO_ATTRIBUTE, radiusUserInfoGetter);
    }

    public static void context(KeycloakSession session,
                               IRadiusUserInfoGetter radiusUserInfoGetter) {
        IRadiusUserInfo radiusUserInfo = radiusUserInfoGetter.getRadiusUserInfo();
        Resteasy.getProvider().pushContext(ClientConnection.class,
                radiusUserInfo.getClientConnection());
        Resteasy.pushContext(KeycloakSession.class, session);
        Resteasy.pushContext(KeycloakTransaction.class,
                session.getTransactionManager());
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add(HttpHeaders.USER_AGENT, "Radius/v0.01 rev. 001 (" + radiusUserInfo
                .getClientConnection()
                .getLocalAddr() + ")");
        Resteasy.pushContext(HttpHeaders.class, new ResteasyHttpHeaders(headers));
        session.getContext().setConnection(radiusUserInfo.getClientConnection());
        session.getContext().setRealm(radiusUserInfo.getRealmModel());
        session.getContext().setClient(radiusUserInfo.getClientModel());

    }
}
