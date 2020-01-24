package com.github.vzakharchenko.radius.models;

import java.io.Serializable;

public class RadiusSecretData implements Serializable {
    private String password;

    public RadiusSecretData() {
    }

    public RadiusSecretData(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
