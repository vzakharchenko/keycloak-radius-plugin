package ua.zaskarius.keycloak.plugins.radius.models;


import java.util.List;

import static ua.zaskarius.keycloak.plugins.radius.radius.server.RadiusServerProviderFactory.RADIUS_PROVIDER;

public class RadiusConfigModel {
    private String provider = RADIUS_PROVIDER;
    private String sharedSecret;
    private int authPort = 1812;
    private int accountPort = 1813;
    private List<RadiusAccessModel> radiusIpAccess;
    private boolean useRadius;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
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

    public List<RadiusAccessModel> getRadiusIpAccess() {
        return radiusIpAccess;
    }

    public void setRadiusIpAccess(List<RadiusAccessModel> radiusIpAccess) {
        this.radiusIpAccess = radiusIpAccess;
    }

    public boolean isUseRadius() {
        return useRadius;
    }

    public void setUseRadius(boolean useRadius) {
        this.useRadius = useRadius;
    }
}
