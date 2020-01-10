package ua.zaskarius.keycloak.plugins.radius.configuration;

import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusConnectionProvider;
import ua.zaskarius.keycloak.plugins.radius.radius.provider.RadiusRadiusProviderFactory;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.keycloak.provider.ProviderConfigProperty.*;

public class RadiusCommonSettingFactory extends AbstractFlowConfigurationItem {
    public static final String RADIUS_PROVIDERS = "radius_providers";
    public static final String USE_RADIUS = "use_radius";
    public static final String RADIUS_CLIENTS = "radius_clients";
    public static final ProviderConfigProperty PROVIDER_CONFIG_PROPERTY =
            new ProviderConfigProperty(RADIUS_PROVIDERS,
                    "Select Radius Connection Provider",
                    "Select Radius Connection Provider",
                    LIST_TYPE,
                    RadiusRadiusProviderFactory.KEYCLOAK_RADIUS_SERVER);
    public static final String RADIUS_PROVIDER_SETTINGS = "Radius Provider Settings";

    @Override
    public String getId() {
        return RADIUS_PROVIDER_SETTINGS;
    }

    @Override
    public String getReferenceCategory() {
        return PasswordCredentialModel.TYPE;
    }


    @Override
    public String getDisplayType() {
        return "Radius Provider Settings";
    }

    @Override
    public String getHelpText() {
        return "";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Arrays.asList(
                new ProviderConfigProperty(USE_RADIUS,
                        "Use Radius Server",
                        "Use Radius Server",
                        BOOLEAN_TYPE,
                        "false"),
                new ProviderConfigProperty(RADIUS_CLIENTS,
                        "Clients which use Radius Server",
                        "Clients which use Radius Server",
                        STRING_TYPE,
                        ""),
                PROVIDER_CONFIG_PROPERTY
        );
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        List<ProviderFactory> providerFactories = factory
                .getProviderFactories(IRadiusConnectionProvider.class);
        if (providerFactories != null) {
            List<String> options = providerFactories.stream()
                    .map(ProviderFactory::getId)
                    .collect(Collectors.toList());
            PROVIDER_CONFIG_PROPERTY.setOptions(options);
        }

    }
}
