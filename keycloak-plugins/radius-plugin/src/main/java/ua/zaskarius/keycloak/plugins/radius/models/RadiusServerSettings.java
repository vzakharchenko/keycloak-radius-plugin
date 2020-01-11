package ua.zaskarius.keycloak.plugins.radius.models;

import java.util.Map;

public class RadiusServerSettings {
    private String secret;
    private int authPort;
    private boolean isUseRadius;
    private int accountPort;
    private String provider;
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

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Map<String, String> getAccessMap() {
        return accessMap;
    }

    public void setAccessMap(Map<String, String> accessMap) {
        this.accessMap = accessMap;
    }

    public boolean isUseRadius() {
        return isUseRadius;
    }

    public void setUseRadius(boolean useRadius) {
        isUseRadius = useRadius;
    }
}
