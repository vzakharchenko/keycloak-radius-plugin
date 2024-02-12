package com.github.vzakharchenko.radius.radius.handlers.protocols;

import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoGetter;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;


public class CHAPProtocol extends AbstractAuthProtocol {

    private static final Logger LOGGER = Logger
            .getLogger(CHAPProtocol.class);

    private static final int CHAP_PASSWORD = 3;
    private static final int CHAP_CHALLENGE = 60;

    public CHAPProtocol(AccessRequest accessRequest, KeycloakSession session) {
        super(accessRequest, session);
    }

    @Override
    public ProtocolType getType() {
        return ProtocolType.CHAP;
    }

    @Override
    protected void answer(RadiusPacket answer, IRadiusUserInfoGetter radiusUserInfoGetter) {
        // do nothing
    }

    private MessageDigest getMd5Digest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e); // never happen
        }
    }

    private byte[] computeChapPassword(byte chapId, String plaintextPw, byte[] chapChallenge) {
        MessageDigest md5 = getMd5Digest();
        md5.update(chapId);
        md5.update(plaintextPw.getBytes(UTF_8));
        md5.update(chapChallenge);

        return ByteBuffer.allocate(17)
                .put(chapId)
                .put(md5.digest())
                .array();
    }

    private boolean verifyChapPassword(String plaintext) {
        RadiusAttribute chapPasswordA = accessRequest.getAttribute(CHAP_PASSWORD);
        RadiusAttribute chapChallengeA = accessRequest.getAttribute(CHAP_CHALLENGE);
        if (chapPasswordA != null) {
            byte[] chapPassword = chapPasswordA.getValue();
            byte[] chapChallenge = chapChallengeA != null ?
                    chapChallengeA.getValue() : accessRequest.getAuthenticator();
            if (plaintext == null || plaintext.isEmpty()) {
                LOGGER.warn("plaintext must not be empty");
            } else if (chapChallenge == null) {
                LOGGER.warn("CHAP challenge is null");
            } else if (chapPassword == null || chapPassword.length != 17) {
                LOGGER.warn("CHAP password must be 17 bytes");
            } else {
                return Arrays.equals(chapPassword,
                        computeChapPassword(chapPassword[0], plaintext, chapChallenge));
            }
        }
        return false;
    }

    @Override
    public boolean verifyProtocolPassword(String password) {
        return verifyChapPassword(password);
    }
}
