package ua.zaskarius.keycloak.plugins.radius.providers;

import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.Provider;

public interface IRadiusConnectionProvider extends Provider {
    void createIfNotExists(RealmModel realmModel,
                           UserModel userModel,
                           String password);

    void deleteUser(RealmModel realmModel,
                    String userId);

    String fieldName();

    String fieldPassword();

    String getPassword(RealmModel realmModel,
                       UserModel userModel);

    boolean init(RealmModel realmModel);
}
