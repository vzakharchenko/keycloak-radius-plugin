package com.github.vzakharchenko.radius.models;

public class RadSecSettingsModel {
    private String privateKey = "";
    private String certificate = "";
    private int numberThreads = 8;
    private boolean useRadSec;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public boolean isUseRadSec() {
        return useRadSec;
    }

    public void setUseRadSec(boolean useRadSec) {
        this.useRadSec = useRadSec;
    }

    public int getNumberThreads() {
        return numberThreads;
    }

    public void setNumberThreads(int numberThreads) {
        this.numberThreads = numberThreads;
    }
}
