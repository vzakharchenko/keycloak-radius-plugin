package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.keycloak.models.KeycloakSession;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;

public interface IRadiusConfiguration {

    RadiusServerSettings getRadiusSettings(KeycloakSession session);
}
