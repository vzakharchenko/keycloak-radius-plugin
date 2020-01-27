package com.github.vzakharchenko.radius.radius.handlers.attributes;

import org.keycloak.models.UserModel;
import org.tinyradius.packet.AccessRequest;

import java.util.HashSet;
import org.keycloak.models.KeycloakSession;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UserKeycloakAttributes extends AbstractKeycloakAttributes<UserModel> {

    public UserKeycloakAttributes(KeycloakSession session, AccessRequest accessRequest) {
        super(session, accessRequest);
    }

    @Override
    protected KeycloakAttributesType getType() {
        return KeycloakAttributesType.USER;
    }

    @Override
    protected Set<UserModel> getKeycloakTypes() {
        Set<UserModel> userModels = new HashSet<>();
        userModels.add(radiusUserInfoGetter.getRadiusUserInfo().getUserModel());
        return userModels;
    }

    @Override
    protected Map<String, Set<String>> getAttributes(UserModel userModel) {
        return userModel.getAttributes().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue() != null ?
                                new HashSet<>(entry.getValue()) : new HashSet<>()));
    }

}
