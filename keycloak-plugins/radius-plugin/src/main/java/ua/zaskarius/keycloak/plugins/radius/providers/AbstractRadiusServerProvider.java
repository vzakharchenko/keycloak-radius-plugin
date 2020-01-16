package ua.zaskarius.keycloak.plugins.radius.providers;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.tinyradius.packet.PacketEncoder;
import ua.zaskarius.keycloak.plugins.radius.radius.dictionary.DictionaryLoader;

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
