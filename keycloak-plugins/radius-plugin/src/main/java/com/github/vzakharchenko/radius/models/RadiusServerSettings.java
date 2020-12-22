package com.github.vzakharchenko.radius.models;

import java.util.Map;

public class RadiusServerSettings {
    private String secret;
    private int authPort;
    private boolean useUdpRadius;
    private int accountPort;
    private int numberThreads;
    private boolean otp;
    private RadSecSettings radSecSettings;
    private CoASettings coASettings;
    private Map<String, String> accessMap;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
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

    public Map<String, String> getAccessMap() {
        return accessMap;
    }

    public void setAccessMap(Map<String, String> accessMap) {
        this.accessMap = accessMap;
    }

    public boolean isUseUdpRadius() {
        return useUdpRadius;
    }

    public void setUseUdpRadius(boolean useUdpRadius) {
        this.useUdpRadius = useUdpRadius;
    }

    public RadSecSettings getRadSecSettings() {
        return radSecSettings;
    }

    public void setRadSecSettings(RadSecSettings radSecSettings) {
        this.radSecSettings = radSecSettings;
    }

    public int getNumberThreads() {
        return numberThreads;
    }

    public void setNumberThreads(int numberThreads) {
        this.numberThreads = numberThreads;
    }

    public CoASettings getCoASettings() {
        return coASettings;
    }

    public void setCoASettings(CoASettings coASettings) {
        this.coASettings = coASettings;
    }

    public boolean isOtp() {
        return otp;
    }

    public void setOtp(boolean otp) {
        this.otp = otp;
    }
}
