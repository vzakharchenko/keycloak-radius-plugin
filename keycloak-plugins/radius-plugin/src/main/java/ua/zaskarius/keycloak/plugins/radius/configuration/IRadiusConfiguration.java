package ua.zaskarius.keycloak.plugins.radius.configuration;

import ua.zaskarius.keycloak.plugins.radius.models.RadiusServerSettings;

public interface IRadiusConfiguration {

    RadiusServerSettings getRadiusSettings();
}
