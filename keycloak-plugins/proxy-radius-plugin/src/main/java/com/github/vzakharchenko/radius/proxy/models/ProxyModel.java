package com.github.vzakharchenko.radius.proxy.models;

public class ProxyModel {
    private String address;
    private int port;
    private String secret;

    public ProxyModel(String address, int port, String secret) {
        this.address = address;
        this.port = port;
        this.secret = secret;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getSecret() {
        return secret;
    }

    public boolean isValid() {
        return address != null && port > 0 && secret != null;
    }
}
