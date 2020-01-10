package ua.zaskarius.keycloak.plugins.radius.models;

import java.util.List;
@Deprecated
public class RadiusCommonSettings {
    private String provider;

    private String id;

    private String executionId;

    private boolean useRadius;

    private List<String> clients;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isUseRadius() {
        return useRadius;
    }

    public void setUseRadius(boolean useRadius) {
        this.useRadius = useRadius;
    }

    public List<String> getClients() {
        return clients;
    }

    public void setClients(List<String> clients) {
        this.clients = clients;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
}
