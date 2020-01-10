package ua.zaskarius.keycloak.plugins.radius.models;

public class ConfigurationRepresentation {

    private String id;

    private boolean start;

    private int authPort;

    private int accountPort;

    private String radiusShared;

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
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

    public String getRadiusShared() {
        return radiusShared;
    }

    public void setRadiusShared(String radiusShared) {
        this.radiusShared = radiusShared;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
