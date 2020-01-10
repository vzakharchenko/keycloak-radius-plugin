package ua.zaskarius.keycloak.plugins.radius.providers;

import org.keycloak.provider.ProviderFactory;

public interface IRadiusAttributesProviderFactory
        <T extends IRadiusAttributeProvider> extends ProviderFactory<T> {
}
