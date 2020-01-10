// CHECKSTYLE:OFF
package ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.mschapv2;

import org.jboss.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import static ua.zaskarius.keycloak.plugins.radius.radius.handlers.protocols.mschapv2.ProtocolMagicUtils.*;

public final class MSCHAPHelper {

    private static final Logger LOGGER = Logger
            .getLogger(MSCHAPHelper.class);


    /**
     * Random number generator.
     */
    private static SecureRandom random = new SecureRandom();



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
        final int authVectorLength = 16;
        final int authPasswordLength = authVectorLength;
        final int maxStringLength = 254;

        // NOTE This could be dodgy!
        int saltOffset = 0;

        // byte digest[] = new byte[authVectorLength];
        byte passwd[] = new byte[maxStringLength + authVectorLength];
        int len;

        /*
         * Be paranoid.
         */
        if (room > 253) {
            room = 253;
        }

        /*
         * Account for 2 bytes of the salt, and round the room available down to
         * the nearest multiple of 16. Then, subtract one from that to account
         * for the length byte, and the resulting number is the upper bound on
         * the data to copy.
         *
         * We could short-cut this calculation just be forcing inlen to be no
         * more than 239. It would work for all VSA's, as we don't pack multiple
         * VSA's into one attribute.
         *
         * However, this calculation is more general, if a little complex. And
         * it will work in the future for all possible kinds of weird attribute
         * packing.
         */
        room -= 2;
        room -= (room & 0x0f);
        room--;

        int inlen = input.length;

        if (inlen > room) {
            inlen = room;
        }

        /*
         * Length of the encrypted data is password length plus one byte for the
         * length of the password.
         */
        len = inlen + 1;
        if ((len & 0x0f) != 0) {
            len += 0x0f;
            len &= ~0x0f;
        }

        /*
         * Copy the password over.
         */
        System.arraycopy(input, 0, passwd, 3, inlen);
        // memcpy(passwd + 3, input, inlen);
        for (int i = 3 + inlen; i < passwd.length - 3 - inlen; i++) {
            passwd[i] = 0;
        }
        // memset(passwd + 3 + inlen, 0, passwd.length - 3 - inlen);

        /*
         * Generate salt. The RFC's say:
         *
         * The high bit of salt[0] must be set, each salt in a packet should be
         * unique, and they should be random
         *
         * So, we set the high bit, add in a counter, and then add in some
         * CSPRNG data. should be OK..
         */
        passwd[0] = (byte) (0x80 | (((saltOffset++) & 0x0f) << 3) | (
                random.generateSeed(1)[0] & 0x07));
        passwd[1] = random.generateSeed(1)[0];
        passwd[2] = (byte) inlen; /* length of the password string */

        MessageDigest md5Digest = null;
        MessageDigest originalDigest = null;

        MessageDigest currentDigest = null;

        try {
            md5Digest = MessageDigest.getInstance("MD5");
            originalDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException("md5 digest not available", nsae);
        }

        md5Digest.update(secret);
        originalDigest.update(secret);

        currentDigest = md5Digest;

        md5Digest.update(vector, 0, authVectorLength);
        md5Digest.update(passwd, 0, 2);

        for (int n = 0; n < len; n += authPasswordLength) {
            if (n > 0) {
                currentDigest = originalDigest;

                currentDigest.update(passwd, 2 + n - authPasswordLength, authPasswordLength);
            }

            byte digest[] = currentDigest.digest();

            for (int i = 0; i < authPasswordLength; i++) {
                passwd[i + 2 + n] ^= digest[i];
            }
        }
        byte output[] = new byte[len + 2];
        System.arraycopy(passwd, 0, output, 0, len + 2);

        return output;
    }

    private static void parity_key(byte[] szOut, final byte[] szIn, final int offset) {
        int i;
        int cNext = 0;
        int cWorking = 0;

        for (i = 0; i < 7; i++) {
            cWorking = 0xFF & szIn[i + offset];
            szOut[i] = (byte) (((cWorking >> i) | cNext | 1) & 0xff);
            cWorking = 0xFF & szIn[i + offset];
            cNext = ((cWorking << (7 - i)));
        }

        szOut[i] = (byte) (cNext | 1);
    }

    private static byte[] unicode(byte[] in) {
        byte b[] = new byte[in.length * 2];
        for (int i = 0; i < b.length; i++) {
            b[i] = 0;
        }
        for (int i = 0; i < in.length; i++) {
            b[(2 * i)] = in[i];
        }
        return b;
    }

    public static byte[] ChallengeHash(final byte[] PeerChallenge,
                                        final byte[] AuthenticatorChallenge,
                                        final byte[] UserName)
            throws NoSuchAlgorithmException {
        byte Challenge[] = new byte[8];
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(PeerChallenge, 0, 16);
        md.update(AuthenticatorChallenge, 0, 16);
        md.update(UserName, 0, UserName.length);
        System.arraycopy(md.digest(), 0, Challenge, 0, 8);
        return Challenge;
    }

    public static byte[] NtPasswordHash(byte[] Password)
            throws NoSuchAlgorithmException {
        byte PasswordHash[] = new byte[16];
        byte uniPassword[] = unicode(Password);
        MessageDigest md = MessageDigest.getInstance("MD4");
        md.update(uniPassword, 0, uniPassword.length);
        System.arraycopy(md.digest(), 0, PasswordHash, 0, 16);
        return PasswordHash;
    }

    private static void DesEncrypt(byte[] Clear, int clearOffset,
                                   byte[] Key, int keyOffset, byte[] Cypher, int cypherOffset) {
        byte szParityKey[] = new byte[8];
        parity_key(szParityKey, Key, keyOffset);

        try {
            KeySpec ks = new DESKeySpec(szParityKey);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            SecretKey sk = skf.generateSecret(ks);
            Cipher c = Cipher.getInstance("DES/CBC/NoPadding");
            IvParameterSpec ips = new IvParameterSpec(new byte[] {0, 0, 0, 0, 0, 0, 0, 0});
            c.init(Cipher.ENCRYPT_MODE, sk, ips);

            c.doFinal(Clear, clearOffset, Clear
                    .length - clearOffset, Cypher, cypherOffset);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    private static byte[] ChallengeResponse(final byte[] Challenge, final byte[] PasswordHash) {
        byte Response[] = new byte[24];
        byte ZPasswordHash[] = new byte[21];

        for (int i = 0; i < 16; i++) {
            ZPasswordHash[i] = PasswordHash[i];
        }

        for (int i = 16; i < 21; i++) {
            ZPasswordHash[i] = 0;
        }

        DesEncrypt(Challenge, 0, ZPasswordHash, 0, Response, 0);
        DesEncrypt(Challenge, 0, ZPasswordHash, 7, Response, 8);
        DesEncrypt(Challenge, 0, ZPasswordHash, 14, Response, 16);

        return Response;
    }

    private static byte[] GenerateNTResponse(byte[] AuthenticatorChallenge,
                                             byte[] PeerChallenge, byte[] UserName,
                                             byte[] Password) throws NoSuchAlgorithmException {
        byte Challenge[] = ChallengeHash(PeerChallenge, AuthenticatorChallenge, UserName);
        byte PasswordHash[] = NtPasswordHash(Password);
        return ChallengeResponse(Challenge, PasswordHash);
    }

    public static boolean verifyMSCHAPv2(byte[] UserName,
                                         byte[] Password,
                                         byte[] Challenge,
                                         byte[] Response) throws NoSuchAlgorithmException {
        byte peerChallenge[] = new byte[16];
        byte sentNtResponse[] = new byte[24];

        System.arraycopy(Response, 2, peerChallenge, 0, 16);
        System.arraycopy(Response, 26, sentNtResponse, 0, 24);

        byte ntResponse[] = GenerateNTResponse(Challenge, peerChallenge, UserName, Password);

        return Arrays.equals(ntResponse, sentNtResponse);
    }


    public static byte[] HashNtPasswordHash(byte[] PasswordHash)
            throws NoSuchAlgorithmException {
        byte PasswordHashHash[] = new byte[16];
        MessageDigest md = MessageDigest.getInstance("MD4");
        md.update(PasswordHash, 0, 16);
        System.arraycopy(md.digest(), 0, PasswordHashHash, 0, 16);
        return PasswordHashHash;
    }
}
