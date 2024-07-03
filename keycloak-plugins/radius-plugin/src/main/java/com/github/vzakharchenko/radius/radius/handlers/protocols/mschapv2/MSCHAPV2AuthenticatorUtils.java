package com.github.vzakharchenko.radius.radius.handlers.protocols.mschapv2;

import com.github.vzakharchenko.radius.radius.RadiusLibraryUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.github.vzakharchenko.radius.radius.handlers.protocols.mschapv2.ProtocolMagicUtils.MSCHAPV2_AUTHENTICATOR_MAGIC_1;
import static com.github.vzakharchenko.radius.radius.handlers.protocols.mschapv2.ProtocolMagicUtils.MSCHAPV2_AUTHENTICATOR_MAGIC_2;

public final class MSCHAPV2AuthenticatorUtils {

    public static final String SHA_1 = "SHA-1";

    private MSCHAPV2AuthenticatorUtils() {
    }

    //CHECKSTYLE:OFF
    public static byte[] generateAuthenticatorResponse(
            byte[] password,
            byte[] ntResponse,
            byte[] peerChallenge,
            byte[] authenticatorChallenge,
            byte[] userName) throws NoSuchAlgorithmException {
        //CHECKSTYLE:ON
        byte[] pBytes = RadiusLibraryUtils.getOrEmpty(password, 16);
        byte[] passwordHashHash = getPasswordHashHash(pBytes);

        MessageDigest md = getMessageDigestSHA1();
        md.update(passwordHashHash, 0, 16);
        md.update(ntResponse, 0, 24);
        md.update(MSCHAPV2_AUTHENTICATOR_MAGIC_1, 0, 39);
        byte[] digest = md.digest();
        byte[] challenge = MSCHAPHelper.challengeHash(peerChallenge,
                authenticatorChallenge, userName);
        MessageDigest md2 = getMessageDigestSHA1();
        md2.update(digest, 0, 20);
        md2.update(challenge, 0, 8);
        md2.update(MSCHAPV2_AUTHENTICATOR_MAGIC_2, 0, 41);

        return md2.digest();
    }

    private static MessageDigest getMessageDigestSHA1()
            throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(SHA_1);
    }

    public static byte[] getPasswordHashHash(byte[] password) {
        byte[] passwordHash = MSCHAPHelper.ntPasswordHash(password);
        return MSCHAPHelper.hashNtPasswordHash(passwordHash);
    }

}
