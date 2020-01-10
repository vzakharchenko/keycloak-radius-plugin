package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Arrays;
import java.util.List;

import static org.keycloak.provider.ProviderConfigProperty.PASSWORD;
import static org.keycloak.provider.ProviderConfigProperty.STRING_TYPE;

public class RadiusSettingFactory extends AbstractFlowConfigurationItem {
    public static final String RADIUS_SERVER_SECRET = "radius_server_secret";
    public static final String RADIUS_SERVER_HOST = "radius_server_host";

    public static final String RADIUS_SETTINGS = "Radius Settings";

    @Override
    public String getId() {
        return RADIUS_SETTINGS;
    }

    @Override
    public String getReferenceCategory() {
        return PasswordCredentialModel.TYPE;
    }


    @Override
    public String getDisplayType() {
        return RADIUS_SETTINGS;
    }

    @Override
    public String getHelpText() {
        return "";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Arrays.asList(new ProviderConfigProperty(RADIUS_SERVER_SECRET,
                        "Radius Server shared secret",
                        "Radius Server shared secret",
                        PASSWORD,
                        ""),
                new ProviderConfigProperty(RADIUS_SERVER_HOST,
                        "List of Remote HostNames",
                        "List of Remote HostNames",
                        STRING_TYPE,
                        "")
        );
    }
}
