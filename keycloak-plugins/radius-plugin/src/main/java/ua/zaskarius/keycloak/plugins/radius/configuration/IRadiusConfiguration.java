package ua.zaskarius.keycloak.plugins.radius.configuration;

import ua.zaskarius.keycloak.plugins.radius.models.RadiusCommonSettings;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;
import org.keycloak.models.RealmModel;

public interface IRadiusConfiguration {
    String MIKROTIK_SETTINGS = "Mikrotik Settings";

    RadiusCommonSettings getCommonSettings(RealmModel realmModel);

    RadiusServerSettings getRadiusSettings(RealmModel realmModel);

    boolean isUsedRadius(RealmModel realmModel);

    boolean init(RealmModel realmModel);
}
