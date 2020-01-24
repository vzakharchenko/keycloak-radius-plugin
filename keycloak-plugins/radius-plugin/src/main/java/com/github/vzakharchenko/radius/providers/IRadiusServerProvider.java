package com.github.vzakharchenko.radius.providers;

import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;

public interface IRadiusServerProvider extends Provider {

    boolean init(RealmModel realmModel);
}
