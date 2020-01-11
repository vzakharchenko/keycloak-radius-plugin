package ua.zaskarius.keycloak.plugins.radius.radius.dictionary;

import org.keycloak.models.KeycloakSession;
import org.tinyradius.dictionary.Dictionary;

public interface IDictionaryLoader {
    Dictionary loadDictionary(KeycloakSession session);
}
