package ua.zaskarius.keycloak.plugins.radius.radius.provider;

import ua.zaskarius.keycloak.plugins.radius.event.RadiusEventListenerProviderFactory;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusConnectionProvider;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class RadiusRadiusProvider implements IRadiusConnectionProvider {

    public static final String READ_RADIUS_PASSWORD = "READ_RADIUS_PASSWORD";

    @Override
    public void createIfNotExists(RealmModel realmModel, UserModel userModel, String password) {

    }

    @Override
    public void deleteUser(RealmModel realmModel, String userId) {

    }

    @Override
    public String fieldName() {
        return "preferred_username";
    }

    @Override
    public String fieldPassword() {
        return "s";
    }

    @Override
    public String getPassword(RealmModel realmModel, UserModel userModel) {
        return null;
    }

    @Override
    public boolean init(RealmModel realmModel) {
        boolean changed = false;
        String el = realmModel
                .getEventsListeners()
                .stream().filter(s -> Objects
                        .equals(s, RadiusEventListenerProviderFactory
                                .RADIUS_EVENT_LISTENER))
                .findFirst().orElse(null);
        if (el == null) {
            Set<String> els = new LinkedHashSet<>(realmModel
                    .getEventsListeners());
            els.add(RadiusEventListenerProviderFactory.RADIUS_EVENT_LISTENER);
            realmModel.setEventsListeners(els);
            changed = true;
        }
        return changed;
    }


    @Override
    public void close() {

    }
}
