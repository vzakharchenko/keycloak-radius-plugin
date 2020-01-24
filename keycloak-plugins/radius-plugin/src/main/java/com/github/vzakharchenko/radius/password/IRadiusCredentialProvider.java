package com.github.vzakharchenko.radius.password;

import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public interface IRadiusCredentialProvider<T extends CredentialModel>
        extends CredentialProvider<T> {
    T getPassword(RealmModel realm, UserModel user);
}
