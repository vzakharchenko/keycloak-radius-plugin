package ua.zaskarius.keycloak.plugins.radius.radius.handlers.clientconnection;

import org.keycloak.common.ClientConnection;
import org.tinyradius.packet.RadiusPacket;
import ua.zaskarius.keycloak.plugins.radius.radius.RadiusLibraryUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class RadiusClientConnection implements ClientConnection {

    private final InetSocketAddress inetSocketAddress;
    private final RadiusPacket radiusPacket;

    public RadiusClientConnection(InetSocketAddress inetSocketAddress,
                                  RadiusPacket radiusPacket) {
        this.inetSocketAddress = inetSocketAddress;
        this.radiusPacket = radiusPacket;
    }

    @Override
    public String getRemoteAddr() {
        String attributeValue = RadiusLibraryUtils
                .getAttributeValue(radiusPacket, "NAS-IP-Address");
        if (!attributeValue.isEmpty()) {
            return attributeValue;
        }
        InetAddress address = inetSocketAddress.getAddress();
        if (address != null) {
            return address.getHostAddress();
        } else {
            return null;
        }
    }

    @Override
    public String getRemoteHost() {
        return inetSocketAddress.getHostName();
    }

    @Override
    public int getRemotePort() {
        return inetSocketAddress.getPort();
    }

    @Override
    public String getLocalAddr() {
        String attributeValue = radiusPacket.getAttributeValue("Calling-Station-Id");
        return attributeValue == null ? "" : attributeValue;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }
}
