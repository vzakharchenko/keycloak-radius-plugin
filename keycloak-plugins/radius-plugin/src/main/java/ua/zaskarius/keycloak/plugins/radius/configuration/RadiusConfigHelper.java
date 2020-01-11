package ua.zaskarius.keycloak.plugins.radius.configuration;

import com.google.common.annotations.VisibleForTesting;

public final class RadiusConfigHelper {

    private static IRadiusConfiguration
            configuration = new FileRadiusConfiguration();

    private RadiusConfigHelper() {
    }

    public static IRadiusConfiguration getConfig() {
        return configuration;
    }

    @VisibleForTesting
    public static void setConfiguration(IRadiusConfiguration flowConfiguration) {
        configuration = flowConfiguration;
    }


}
