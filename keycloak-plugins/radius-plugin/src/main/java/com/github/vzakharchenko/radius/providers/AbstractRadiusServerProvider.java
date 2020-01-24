package com.github.vzakharchenko.radius.providers;

import com.github.vzakharchenko.radius.radius.dictionary.DictionaryLoader;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.tinyradius.packet.PacketEncoder;

public abstract class AbstractRadiusServerProvider implements IRadiusServerProvider {

    @Override
    public boolean init(RealmModel realmModel) {
        return false;
    }


    protected PacketEncoder createPacketEncoder(KeycloakSession session) {
        return new PacketEncoder(
                DictionaryLoader.getInstance()
                        .loadDictionary(session));
    }

    @Override
    public void close() {

    }
}
