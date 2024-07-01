package com.github.vzakharchenko.radius.radius.handlers.protocols.mschapv2;

import org.eclipse.angus.mail.auth.MD4;
import org.jboss.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import static com.github.vzakharchenko.radius.RadiusHelper.getRandomByte;
import static com.github.vzakharchenko.radius.radius.handlers.protocols.mschapv2.ProtocolMagicUtils.*;

public final class MSCHAPHelper {

    private static final Logger LOGGER = Logger
            .getLogger(MSCHAPHelper.class);

    private static final int AUTH_VECTOR_LENGTH = 16;
    private static final int MAX_STRING_LENGTH = 254;
    public static final int ROOM_MAX = 253;

    private MSCHAPHelper() {
    }

    /**
     * Generate the MPPE Master key
     *
     * @param ntHashHash
     * @param ntResponse
     * @return
     * @see https://tools.ietf.org/html/rfc3079#section-3
     */
    public static byte[] generateMPPEMasterKey(byte[] ntHashHash,
                                               byte[] ntResponse) {
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(ntHashHash, 0, ntHashHash.length);
            md.update(ntResponse, 0, ntResponse.length);
            md.update(MSCHAP_MAGIC_1, 0, MSCHAP_MAGIC_1.length);

            byte[] digest = md.digest();

            byte[] rv = new byte[16];
            System.arraycopy(digest, 0, rv, 0, 16);

            return rv;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }

    }

    /**
     * Generate the MPPE Asymmetric start key
     *
     * @param masterKey
     * @param keyLength
     * @param isSend
     * @return
     * @see https://tools.ietf.org/html/rfc3079#section-3
     */
    public static byte[] generateMPPEAssymetricStartKey(byte[] masterKey,
                                                        int keyLength, boolean isSend) {
        byte[] magic = (isSend) ? MSCHAP_MAGIC_3 : MSCHAP_MAGIC_2;

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(masterKey, 0, 16);
            md.update(MSCHAP_MAGIC_SHS_PAD_1, 0, 40);
            md.update(magic, 0, 84);
            md.update(MSCHAP_MAGIC_SHS_PAD_2, 0, 40);

            byte[] digest = md.digest();

            byte[] rv = new byte[keyLength];
            System.arraycopy(digest, 0, rv, 0, keyLength);

            return rv;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Generate the MPPE Send Key (Server)
     *
     * @param ntHashHash
     * @param ntResponse
     * @return
     * @see https://tools.ietf.org/html/rfc3079#section-3
     */
    public static byte[] mppeCHAP2GenKeySend128(byte[] ntHashHash, byte[] ntResponse) {
        byte[] masterKey = generateMPPEMasterKey(ntHashHash, ntResponse);

        return generateMPPEAssymetricStartKey(masterKey, 16, true);
    }

    /**
     * Generate the MPPE Receive Key (Server)
     *
     * @param ntHashHash
     * @param ntResponse
     * @return
     * @see https://tools.ietf.org/html/rfc3079#section-3
     */
    public static byte[] mppeCHAP2GenKeyRecv128(byte[] ntHashHash,
                                                byte[] ntResponse) {
        byte[] masterKey = generateMPPEMasterKey(ntHashHash, ntResponse);

        return generateMPPEAssymetricStartKey(masterKey, 16, false);
    }

    private static int getInlen(int room0, byte[] input) {
        int room = room0;
        /* Be paranoid. */
        if (room > ROOM_MAX) {
            room = ROOM_MAX;
        }
        room -= 2;
        room -= (room & 0x0f);
        room--;

        int inlen = input.length;

        if (inlen > room) {
            inlen = room;
        }
        return inlen;
    }

    private static byte[] getPasswd(byte[] input, int inlen) {
        int saltOffset = 0;
        byte[] passwd = new byte[MAX_STRING_LENGTH + AUTH_VECTOR_LENGTH];
        System.arraycopy(input, 0, passwd, 3, inlen);
        for (int i = 3 + inlen; i < passwd.length - 3 - inlen; i++) {
            passwd[i] = 0;
        }

        passwd[0] = (byte) (0x80 | (((saltOffset++) & 0x0f) << 3) | (
                getRandomByte() & 0x07));
        passwd[1] = getRandomByte();
        passwd[2] = (byte) inlen; /* length of the password string */
        return passwd;
    }

    private static MessageDigest getOriginalMessageDigest(byte[] secret) {
        try {
            MessageDigest originalDigest = MessageDigest.getInstance("MD5");
            originalDigest.update(secret);
            return originalDigest;
        } catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException("md5 digest not available", nsae);
        }
    }

    private static MessageDigest getMessageDigest(
            byte[] secret, byte[] vector,
            byte[] passwd) {
        try {
            MessageDigest md5Digest;
            md5Digest = MessageDigest.getInstance("MD5");
            md5Digest.update(secret);
            md5Digest.update(vector, 0, AUTH_VECTOR_LENGTH);
            md5Digest.update(passwd, 0, 2);
            return md5Digest;
        } catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException("md5 digest not available", nsae);
        }
    }


    private static byte[] getOutput(int len, byte[] passwd) {
        byte[] output = new byte[len + 2];
        System.arraycopy(passwd, 0, output, 0, len + 2);
        return output;
    }

    /**
     * Encrypt an MPPE password
     * Adapted from FreeRadius src/lib/radius.x:make_tunnel_password
     *
     * @param input  the data to encrypt
     * @param room   not sure - just set it to something greater than 255
     * @param secret the Radius secret for this packet
     * @param vector the auth challenge
     * @return
     * @see ??
     */
    public static byte[] generateEncryptedMPPEPassword(byte[] input,
                                                       int room,
                                                       byte[] secret, byte[] vector) {


        int len;
        int inlen = getInlen(room, input);
        len = inlen + 1;
        if ((len & 0x0f) != 0) {
            len += 0x0f;
            len &= ~0x0f;
        }
        byte[] passwd = getPasswd(input, inlen);
        MessageDigest originalDigest = getOriginalMessageDigest(secret);
        MessageDigest currentDigest = getMessageDigest(secret, vector, passwd);
        for (int n = 0; n < len; n += AUTH_VECTOR_LENGTH) {
            if (n > 0) {
                currentDigest = originalDigest;
                currentDigest.update(passwd, 2 + n - AUTH_VECTOR_LENGTH, AUTH_VECTOR_LENGTH);
            }
            byte[] digest = currentDigest.digest();
            for (int i = 0; i < AUTH_VECTOR_LENGTH; i++) {
                passwd[i + 2 + n] ^= digest[i];
            }
        }
        return getOutput(len, passwd);
    }

    private static void parityKey(byte[] szOut, final byte[] szIn, final int offset) {
        int i;
        int cNext = 0;
        int cWorking;

        for (i = 0; i < 7; i++) {
            cWorking = 0xFF & szIn[i + offset];
            szOut[i] = (byte) (((cWorking >> i) | cNext | 1) & 0xff);
            cWorking = 0xFF & szIn[i + offset];
            cNext = ((cWorking << (7 - i)));
        }

        szOut[i] = (byte) (cNext | 1);
    }

    private static byte[] unicode(byte[] in) {
        byte[] b = new byte[in.length * 2];
        for (int i = 0; i < in.length; i++) {
            b[(2 * i)] = in[i];
        }
        return b;
    }

    public static byte[] challengeHash(final byte[] peerChallenge,
                                       final byte[] authenticatorChallenge,
                                       final byte[] userName)
            throws NoSuchAlgorithmException {
        byte[] challenge = new byte[8];
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(peerChallenge, 0, 16);
        md.update(authenticatorChallenge, 0, 16);
        md.update(userName, 0, userName.length);
        System.arraycopy(md.digest(), 0, challenge, 0, 8);
        return challenge;
    }

    public static byte[] ntPasswordHash(byte[] password) {
        return md4Digest(unicode(password));
    }

    //CHECKSTYLE:OFF
    private static void desEncrypt(byte[] clear,
                                   byte[] key, int keyOffset, byte[] cypher, int cypherOffset) {
        //CHECKSTYLE:ON
        byte[] szParityKey = new byte[8];
        parityKey(szParityKey, key, keyOffset);

        try {
            KeySpec ks = new DESKeySpec(szParityKey);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            SecretKey sk = skf.generateSecret(ks);
            Cipher c = Cipher.getInstance("DES/CBC/NoPadding");
            IvParameterSpec ips = new IvParameterSpec(new byte[8]);
            c.init(Cipher.ENCRYPT_MODE, sk, ips);

            c.doFinal(clear, 0, clear
                    .length, cypher, cypherOffset);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }


    private static byte[] challengeResponse(final byte[] challenge, final byte[] passwordHash) {
        byte[] response = new byte[24];
        byte[] zPasswordHash = new byte[21];

        System.arraycopy(passwordHash, 0, zPasswordHash, 0, 16);

        for (int i = 16; i < 21; i++) {
            zPasswordHash[i] = 0;
        }

        desEncrypt(challenge, zPasswordHash, 0, response, 0);
        desEncrypt(challenge, zPasswordHash, 7, response, 8);
        desEncrypt(challenge, zPasswordHash, 14, response, 16);

        return response;
    }

    private static byte[] generateNTResponse(byte[] authenticatorChallenge,
                                             byte[] peerChallenge, byte[] userName,
                                             byte[] password) throws NoSuchAlgorithmException {
        byte[] challenge = challengeHash(peerChallenge, authenticatorChallenge, userName);
        byte[] passwordHash = ntPasswordHash(password);
        return challengeResponse(challenge, passwordHash);
    }

    public static boolean verifyMSCHAPv2(byte[] userName,
                                         byte[] password,
                                         byte[] challenge,
                                         byte[] response) throws NoSuchAlgorithmException {
        byte[] peerChallenge = new byte[16];
        byte[] sentNtResponse = new byte[24];

        System.arraycopy(response, 2, peerChallenge, 0, 16);
        System.arraycopy(response, 26, sentNtResponse, 0, 24);

        byte[] ntResponse = generateNTResponse(challenge, peerChallenge, userName, password);

        return Arrays.equals(ntResponse, sentNtResponse);
    }

    public static byte[] hashNtPasswordHash(byte[] passwordHash) {
        // ensure that only the first 16 bytes are used, if present, ignore the rest
        byte[] passwordHash16 = new byte[16];
        System.arraycopy(passwordHash, 0, passwordHash16, 0, 16);
        return md4Digest(passwordHash16);
    }

    private static byte[] md4Digest(byte[] value) {
        return new MD4().digest(value); // MD4 instances are not thread-safe
    }
}
