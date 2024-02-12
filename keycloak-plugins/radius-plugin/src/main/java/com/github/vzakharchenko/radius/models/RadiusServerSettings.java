package com.github.vzakharchenko.radius.models;

import com.github.vzakharchenko.radius.radius.handlers.protocols.ProtocolType;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class RadiusServerSettings {
    private String secret;
    private int authPort;
    private boolean useUdpRadius;
    private int accountPort;
    private int numberThreads;
    private final Set<ProtocolType> otpWithoutPassword = EnumSet.noneOf(ProtocolType.class);
    private String externalDictionary;
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

    public boolean isOtpWithoutPassword(ProtocolType type) {
        return otpWithoutPassword.contains(type);
    }

    public void addOtpWithoutPassword(ProtocolType type) {
        this.otpWithoutPassword.add(type);
    }

    public void removeOtpWithoutPassword(ProtocolType type) {
        this.otpWithoutPassword.remove(type);
    }

    public String getExternalDictionary() {
        return externalDictionary;
    }

    public void setExternalDictionary(String externalDictionary) {
        this.externalDictionary = externalDictionary;
    }
}
