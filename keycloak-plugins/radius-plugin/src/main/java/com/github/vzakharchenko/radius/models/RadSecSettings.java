package com.github.vzakharchenko.radius.models;

public class RadSecSettings {
    private String privKey = "";
    private String cert = "";
    private int nThreads = 8;
    private boolean useRadSec;

    public String getPrivKey() {
        return privKey;
    }

    public void setPrivKey(String privKey) {
        this.privKey = privKey;
    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    public boolean isUseRadSec() {
        return useRadSec;
    }

    public void setUseRadSec(boolean useRadSec) {
        this.useRadSec = useRadSec;
    }

    public int getnThreads() {
        return nThreads;
    }

    public void setnThreads(int nThreads) {
        this.nThreads = nThreads;
    }
}
