package ua.zaskarius.keycloak.plugins.radius.models;

import java.util.List;

public class RadiusServerSettings {
    private String secret;
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
}
