package com.github.vzakharchenko.radius.radius.handlers;

import com.github.vzakharchenko.radius.configuration.RadiusConfigHelper;
import com.github.vzakharchenko.radius.models.RadiusServerSettings;
import org.jboss.logging.Logger;
import org.tinyradius.server.SecretProvider;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class KeycloakSecretProvider implements SecretProvider {

    private static final Logger LOGGER = Logger.getLogger(KeycloakSecretProvider.class);


    private String getSharedByIp(RadiusServerSettings radiusSettings,
                                 String hostAddress
    ) {
        String secret = radiusSettings.getAccessMap().get(hostAddress);
        if (secret != null) {
            LOGGER.info("RADIUS " + hostAddress + " connected");
        }
        return secret;
    }

    @Override
    public String getSharedSecret(InetSocketAddress address) {
        InetAddress inetAddress = address
                .getAddress();
        if (inetAddress == null) {
            return null;
        }
        String hostAddress = inetAddress
                .getHostAddress();
        RadiusServerSettings radiusSettings = RadiusConfigHelper.getConfig().getRadiusSettings();
        if (radiusSettings.getAccessMap() != null) {
            String secret = getSharedByIp(radiusSettings, hostAddress);
            if (secret != null) {
                return secret;
            }
        }
        String settingsSecret = radiusSettings.getSecret();
        if (settingsSecret == null || settingsSecret.isEmpty()) {
            LOGGER.warn("RADIUS " + address.getAddress().getHostAddress() + " disconnected");
        }
        return settingsSecret;
    }


}
