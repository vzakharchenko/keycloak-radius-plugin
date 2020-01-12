package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals.AttributeConditional;

import java.util.*;
import java.util.stream.Collectors;

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
    protected Map<String, Set<String>> getAttributes(UserModel userModel) {
        return userModel.getAttributes().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue() != null ?
                                new HashSet<>(entry.getValue()) : new HashSet<>()));
    }

    @Override
    protected List<AttributeConditional<UserModel>> getAttributeConditional() {
        return Collections.EMPTY_LIST;
    }
}
