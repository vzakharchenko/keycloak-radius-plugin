package ua.zaskarius.keycloak.plugins.radius.configuration;

public final class RadiusConfigHelper {

    private static IRadiusConfiguration
            configuration = new FlowRadiusConfiguration();

    private RadiusConfigHelper() {
    }

    public static IRadiusConfiguration getConfig() {
        return configuration;
    }

    public static void setFlowConfiguration(IRadiusConfiguration flowConfiguration) {
        configuration = flowConfiguration;
    }
}
