package com.github.vzakharchenko.radius.radius.handlers.attributes;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.tinyradius.packet.AccessRequest;

import java.util.HashMap;
import java.util.HashSet;
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
        Map<String, Set<String>> collect = userModel.getAttributes().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue() != null ?
                                new HashSet<>(entry.getValue()) : new HashSet<>()));
        if (conditionalAttributes(collect)) {
            return collect;
        }
        return new HashMap<>();
    }

}
