package ua.zaskarius.keycloak.plugins.radius.radius.handlers.session;

import java.util.Arrays;

public enum RadiusAccountState {
    START("Start"),
    STOP("Stop"),
    ALIVE("Alive"),
    INTERIM_UPDATE("Interim-Update");

    private String radiusState;

    RadiusAccountState(String radiusState) {
        this.radiusState = radiusState;
    }

    public String getRadiusState() {
        return radiusState;
    }

    public static RadiusAccountState getByRadiusState(String value) {
        return Arrays.stream(RadiusAccountState.values())
                .filter(radiusAccountState -> radiusAccountState
                        .getRadiusState().equalsIgnoreCase(value)).findFirst().orElse(START);
    }
}
