package com.github.vzakharchenko.radius.models.file;

public class RadiusAccessModel {
    private String ip;
    private String sharedSecret;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }
}
