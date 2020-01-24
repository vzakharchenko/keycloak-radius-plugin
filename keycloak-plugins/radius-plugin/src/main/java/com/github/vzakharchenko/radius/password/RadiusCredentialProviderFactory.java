package com.github.vzakharchenko.radius.password;

import org.keycloak.credential.CredentialProviderFactory;
import org.keycloak.models.KeycloakSession;


public class RadiusCredentialProviderFactory implements
        CredentialProviderFactory<RadiusCredentialProvider> {
    public static final String RADIUS_PROVIDER_ID = "mikrotik-password";

    @Override
    public RadiusCredentialProvider create(KeycloakSession session) {
        return new RadiusCredentialProvider(session);
    }

    @Override
    public String getId() {
        return RADIUS_PROVIDER_ID;
    }

}
