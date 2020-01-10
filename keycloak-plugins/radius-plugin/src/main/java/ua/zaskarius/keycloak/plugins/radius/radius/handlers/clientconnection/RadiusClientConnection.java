package ua.zaskarius.keycloak.plugins.radius.radius.handlers.clientconnection;

import org.keycloak.common.ClientConnection;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class RadiusClientConnection implements ClientConnection {

    private final InetSocketAddress inetSocketAddress;

    public RadiusClientConnection(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    @Override
    public String getRemoteAddr() {
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
        return "";
    }

    @Override
    public int getLocalPort() {
        return 0;
    }
}
