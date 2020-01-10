package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusCommonSettings;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusConfigModel;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;

public class DBRadiusConfiguration implements IRadiusConfiguration {

    protected DBRadiusConfiguration() {
    }

    @Override
    public RadiusServerSettings getRadiusSettings(KeycloakSession session) {
        RadiusConfigJPA radiusConfigJPA = new RadiusConfigJPA(session);
        RadiusConfigModel config = radiusConfigJPA.getConfig();
        RadiusServerSettings radiusServerSettings = new RadiusServerSettings();
        radiusServerSettings.setSecret(config.getRadiusShared());
        radiusServerSettings.setAccountPort(config.getAccountPort());
        radiusServerSettings.setAccountPort(config.getAccountPort());
        radiusServerSettings.setUseRadius(config.isStart());
        return radiusServerSettings;
    }

    @Override
    public RadiusCommonSettings getCommonSettings(RealmModel realmModel) {
        return null;
    }

    @Override
    public RadiusServerSettings getRadiusSettings(RealmModel realmModel) {
        return null;
    }

    @Override
    public boolean isUsedRadius(RealmModel realmModel) {
        return true;
    }

    @Override
    public boolean isUsedRadius(KeycloakSession session) {
        return getRadiusSettings(session).isUseRadius();
    }

    @Override
    public boolean init(RealmModel realmModel) {
        return false;
    }
}
