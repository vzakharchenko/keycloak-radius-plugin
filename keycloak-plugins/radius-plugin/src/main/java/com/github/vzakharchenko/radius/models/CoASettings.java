package com.github.vzakharchenko.radius.models;

public class CoASettings {
    private int coaPort;
    private boolean useCoAPackage;

    public int getCoaPort() {
        return coaPort;
    }

    public void setCoaPort(int coaPort) {
        this.coaPort = coaPort;
    }

    public boolean isUseCoAPackage() {
        return useCoAPackage;
    }

    public void setUseCoAPackage(boolean useCoAPackage) {
        this.useCoAPackage = useCoAPackage;
    }
}
