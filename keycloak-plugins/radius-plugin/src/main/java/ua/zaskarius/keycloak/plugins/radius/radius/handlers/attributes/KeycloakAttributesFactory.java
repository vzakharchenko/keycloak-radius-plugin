package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAttributeProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusAttributesProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class KeycloakAttributesFactory implements IRadiusAttributeProvider,
        IRadiusAttributesProviderFactory<KeycloakAttributesFactory> {


    public static final String KEYCLOAK_ATTRIBUTES_DEFAULT = "keycloak-attributes-default";

    @Override
    public KeycloakAttributes createKeycloakAttributes(KeycloakSession session,
                                                       KeycloakAttributesType type) {
        KeycloakAttributes keycloakAttributes;
        switch (type) {
            case USER:
                keycloakAttributes = new UserKeycloakAttributes(session);
                break;

            case GROUP:
                keycloakAttributes = new GroupKeycloakAttributes(session);
                break;

            case ROLE:
                keycloakAttributes = new RoleKeycloakAttributes(session);
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
