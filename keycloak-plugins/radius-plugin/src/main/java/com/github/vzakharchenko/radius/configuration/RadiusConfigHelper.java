package com.github.vzakharchenko.radius.configuration;

import com.github.vzakharchenko.radius.models.CoASettings;

public final class RadiusConfigHelper {

    private static IRadiusConfiguration
            configuration = new FileRadiusConfiguration();

    private RadiusConfigHelper() {
    }

    public static IRadiusConfiguration getConfig() {
        return configuration;
    }

    public static CoASettings getCoASettings() {
        return getConfig().getRadiusSettings().getCoASettings();
    }

    // use for testing only
    public static void setConfiguration(IRadiusConfiguration flowConfiguration) {
        configuration = flowConfiguration;
    }


}
