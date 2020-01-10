package ua.zaskarius.keycloak.plugins.radius.radius.handlers.session;

import ua.zaskarius.keycloak.plugins.radius.models.RadiusUserInfo;
import org.keycloak.models.KeycloakSession;

public final class KeycloakSessionUtils {

    private static final String RADIUS_INFO_ATTRIBUTE = "RADIUS_INFO";

    private KeycloakSessionUtils() {
    }

    public static <T> T getSessionAttribute(KeycloakSession session, String name, Class<T> tClass) {
        return session.getAttribute(name, tClass);
    }

    public static RadiusUserInfo getRadiusUserInfo(KeycloakSession session) {
        return getSessionAttribute(session, RADIUS_INFO_ATTRIBUTE, RadiusUserInfo.class);
    }

    public static void addAttribute(KeycloakSession session, String name, Object value) {
        session.setAttribute(name, value);
    }

    public static void addRadiusUserInfo(KeycloakSession session, RadiusUserInfo radiusUserInfo) {
        addAttribute(session, RADIUS_INFO_ATTRIBUTE, radiusUserInfo);
    }
}
