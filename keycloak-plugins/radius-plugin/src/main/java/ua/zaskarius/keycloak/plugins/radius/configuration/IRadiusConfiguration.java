package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusCommonSettings;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;

public interface IRadiusConfiguration {
    String RADIUS_SETTINGS = "Radius Settings";

    RadiusServerSettings getRadiusSettings(KeycloakSession session);

    @Deprecated
    RadiusCommonSettings getCommonSettings(RealmModel realmModel);

    @Deprecated
    RadiusServerSettings getRadiusSettings(RealmModel realmModel);

    @Deprecated
    boolean isUsedRadius(RealmModel realmModel);

    boolean isUsedRadius(KeycloakSession session);

    @Deprecated
    boolean init(RealmModel realmModel);
}
