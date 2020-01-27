package com.github.vzakharchenko.radius.models.file;


import java.util.List;

public class RadiusConfigModel {
    private String sharedSecret;
    private RadSecSettingsModel radsec;
    private CoASettingsModel coa;
    private int authPort = 1812;
    private int accountPort = 1813;
    private int numberThreads = 8;
    private List<RadiusAccessModel> radiusIpAccess;
    private boolean useUdpRadius;

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public int getAuthPort() {
        return authPort;
    }

    public void setAuthPort(int authPort) {
        this.authPort = authPort;
    }

    public int getAccountPort() {
        return accountPort;
    }

    public void setAccountPort(int accountPort) {
        this.accountPort = accountPort;
    }

    public List<RadiusAccessModel> getRadiusIpAccess() {
        return radiusIpAccess;
    }

    public void setRadiusIpAccess(List<RadiusAccessModel> radiusIpAccess) {
        this.radiusIpAccess = radiusIpAccess;
    }

    public boolean isUseUdpRadius() {
        return useUdpRadius;
    }

    public void setUseUdpRadius(boolean useUdpRadius) {
        this.useUdpRadius = useUdpRadius;
    }

    public RadSecSettingsModel getRadsec() {
        return radsec;
    }

    public void setRadsec(RadSecSettingsModel radsec) {
        this.radsec = radsec;
    }

    public int getNumberThreads() {
        return numberThreads;
    }

    public void setNumberThreads(int numberThreads) {
        this.numberThreads = numberThreads;
    }

    public CoASettingsModel getCoa() {
        return coa;
    }

    public void setCoa(CoASettingsModel coa) {
        this.coa = coa;
    }
}
