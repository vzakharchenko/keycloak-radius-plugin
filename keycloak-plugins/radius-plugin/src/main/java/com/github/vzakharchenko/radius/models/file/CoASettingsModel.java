package com.github.vzakharchenko.radius.models.file;

public class CoASettingsModel {
    private int port;
    private boolean useCoA;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isUseCoA() {
        return useCoA;
    }

    public void setUseCoA(boolean useCoA) {
        this.useCoA = useCoA;
    }
}
