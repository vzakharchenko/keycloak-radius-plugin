package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals.AttributeConditional;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;

import java.util.*;

public class UserKeycloakAttributes extends AbstractKeycloakAttributes<UserModel> {

    public UserKeycloakAttributes(KeycloakSession session) {
        super(session);
    }

    @Override
    protected KeycloakAttributesType getType() {
        return KeycloakAttributesType.USER;
    }

    @Override
    protected Set<UserModel> getKeycloakTypes() {
        Set<UserModel> userModels = new HashSet<>();
        userModels.add(radiusUserInfo.getUserModel());
        return userModels;
    }

    @Override
    protected List<String> getAttributes(UserModel userModel,
                                         String attributeName) {
        List<String> attributes = new ArrayList<>();
        userModel.getAttributes().entrySet().stream().filter(entry ->
                attributeName.equalsIgnoreCase(entry.getKey())).forEach(entry -> {
            if (entry.getValue() != null) {
                attributes.addAll(entry.getValue());
            }
        });
        return attributes;
    }

    @Override
    protected List<AttributeConditional<UserModel>> getAttributeConditional() {
        return Collections.EMPTY_LIST;
    }
}
