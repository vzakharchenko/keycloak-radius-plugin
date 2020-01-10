package ua.zaskarius.keycloak.plugins.radius.models;

import java.util.List;

public class RadiusServerSettings {
    private String secret;
    private int authPort;
    private int accountPort;
    private boolean useRadius;
    @Deprecated
    private List<String> url;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
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

    public boolean isUseRadius() {
        return useRadius;
    }

    public void setUseRadius(boolean useRadius) {
        this.useRadius = useRadius;
    }
}
