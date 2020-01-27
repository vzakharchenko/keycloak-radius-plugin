package com.github.vzakharchenko.radius.radius.handlers.session;

import java.util.Arrays;

public enum RadiusAccountState {
    START("Start"),
    STOP("Stop"),
    ALIVE("Alive"),
    UNSUPPORTED(""),
    INTERIM_UPDATE("Interim-Update");

    private String radiusState;

    RadiusAccountState(String radiusState) {
        this.radiusState = radiusState;
    }

    public static RadiusAccountState getByRadiusState(String value) {
        return Arrays.stream(RadiusAccountState.values())
                .filter(radiusAccountState -> radiusAccountState
                        .getRadiusState().equalsIgnoreCase(value)).findFirst().orElse(UNSUPPORTED);
    }

    public String getRadiusState() {
        return radiusState;
    }
}
