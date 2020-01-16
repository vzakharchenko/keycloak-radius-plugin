package ua.zaskarius.keycloak.plugins.radius.models;

public class RadSecSettingsModel {
    private String privateKey = "";
    private String certificate = "";
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
}
