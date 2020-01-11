package ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols;

import ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.mschapv2.MSCHAPV2AuthenticatorUtils;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.Test;

import java.security.NoSuchAlgorithmException;

import static org.testng.Assert.assertEquals;

public class MSCHAPV2AuthenticatorUtilsTest extends AbstractRadiusTest {

    String username = "User";
    String password = "clientPass";

    byte[] ntResponse = new byte[] {
            (byte) 0x82, (byte) 0x30, (byte) 0x9E, (byte) 0xCD, (byte) 0x8D,
            (byte) 0x70, (byte) 0x8B, (byte) 0x5E, (byte) 0xA0, (byte) 0x8F,
            (byte) 0xAA, (byte) 0x39, (byte) 0x81, (byte) 0xCD, (byte) 0x83,
            (byte) 0x54, (byte) 0x42, (byte) 0x33, (byte) 0x11, (byte) 0x4A,
            (byte) 0x3D, (byte) 0x85, (byte) 0xD6, (byte) 0xDF
    };

    byte[] peerChallenge = new byte[] {
            0x21, 0x40, 0x23, 0x24, 0x25,
            0x5E, 0x26, 0x2A, 0x28, 0x29,
            0x5F, 0x2B, 0x3A, 0x33, 0x7C,
            0x7E
    };

    byte[] authenticatorChallenge = new byte[] {
            0x5B, 0x5D, 0x7C, 0x7D, 0x7B,
            0x3F, 0x2F, 0x3E, 0x3C, 0x2C,
            0x60, 0x21, 0x32, 0x26, 0x26,
            0x28
    };

    @Test
    public void test() throws NoSuchAlgorithmException {
        byte[] authResponse = MSCHAPV2AuthenticatorUtils
                .generateAuthenticatorResponse(password.getBytes(),
                        ntResponse,
                        peerChallenge,
                        authenticatorChallenge,
                        username.getBytes());
        assertEquals(Hex.encodeHexString(authResponse), "407a5589115fd0d6209f510fe9c04566932cda56");

    }
}
