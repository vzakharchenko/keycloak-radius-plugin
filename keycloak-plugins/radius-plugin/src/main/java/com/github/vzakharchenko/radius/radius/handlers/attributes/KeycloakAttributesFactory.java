package com.github.vzakharchenko.radius.radius.handlers.attributes;

import com.github.vzakharchenko.radius.providers.IRadiusAttributeProvider;
import com.github.vzakharchenko.radius.providers.IRadiusAttributesProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.tinyradius.packet.AccessRequest;

public class KeycloakAttributesFactory implements IRadiusAttributeProvider,
        IRadiusAttributesProviderFactory<KeycloakAttributesFactory> {


    public static final String KEYCLOAK_ATTRIBUTES_DEFAULT = "keycloak-attributes-default";

    @Override
    public KeycloakAttributes createKeycloakAttributes(AccessRequest accessRequest,
                                                       KeycloakSession session,
                                                       KeycloakAttributesType type) {
        KeycloakAttributes keycloakAttributes;
        switch (type) {
            case USER:
                keycloakAttributes = new UserKeycloakAttributes(session, accessRequest);
                break;

            case GROUP:
                keycloakAttributes = new GroupKeycloakAttributes(session, accessRequest);
                break;

            case ROLE:
                keycloakAttributes = new RoleKeycloakAttributes(session, accessRequest);
                break;

            default:
                throw new IllegalStateException(type + " does not support");

        }
        return keycloakAttributes;
    }


    @Override
    public KeycloakAttributesFactory create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return KEYCLOAK_ATTRIBUTES_DEFAULT;
    }
}
