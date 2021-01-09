package com.github.vzakharchenko.radius.dm.models;

import java.util.List;

public class RadiusInfoModel {
    private String secret;
    private boolean radsec;
    private int coaPort;
    private boolean useCoA;
    private boolean udpRadius;
    private List<RadiusServiceModel> activeSessions;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isRadsec() {
        return radsec;
    }

    public void setRadsec(boolean radsec) {
        this.radsec = radsec;
    }

    public int getCoaPort() {
        return coaPort;
    }

    public void setCoaPort(int coaPort) {
        this.coaPort = coaPort;
    }

    public boolean isUseCoA() {
        return useCoA;
    }

    public void setUseCoA(boolean useCoA) {
        this.useCoA = useCoA;
    }

    public boolean isUdpRadius() {
        return udpRadius;
    }

    public void setUdpRadius(boolean udpRadius) {
        this.udpRadius = udpRadius;
    }

    public List<RadiusServiceModel> getActiveSessions() {
        return activeSessions;
    }

    public void setActiveSessions(List<RadiusServiceModel> activeSessions) {
        this.activeSessions = activeSessions;
    }
}
