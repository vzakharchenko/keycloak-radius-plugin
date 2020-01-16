package ua.zaskarius.keycloak.plugins.radsec.server;

import org.keycloak.models.KeycloakSession;
import ua.zaskarius.keycloak.plugins.radius.providers.AbstractRadiusServerProviderFactory;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProvider;

public class RadSecServerProviderFactory extends AbstractRadiusServerProviderFactory {

    public static final String RADSEC_PROVIDER = "radsec-provider";

    @Override
    public String getId() {
        return RADSEC_PROVIDER;
    }


    @Override
    protected IRadiusServerProvider createInstance(KeycloakSession session) {
        return new RadSecServerProvider(session);
    }

    @Override
    public void postInit(KeycloakSession session, IRadiusServerProvider serverProvider) {

    }
}
