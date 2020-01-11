package ua.zaskarius.keycloak.plugins.radius.providers;

import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;

public interface IRadiusServerProvider extends Provider {
    String fieldName();

    String fieldPassword();

    boolean init(RealmModel realmModel);
}
