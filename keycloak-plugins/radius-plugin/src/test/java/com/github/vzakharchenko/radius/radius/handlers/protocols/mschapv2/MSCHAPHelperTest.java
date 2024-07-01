package com.github.vzakharchenko.radius.radius.handlers.protocols.mschapv2;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.security.*;

public class MSCHAPHelperTest {

    private static final String BOUNCY_CASTLE_PROVIDER_CLASS = //
            "org.bouncycastle.jce.provider.BouncyCastleProvider";
    private static boolean bcInitialized;

    @Test(dataProvider = "hashNtPasswordHash")
    public void testHashNtPasswordHashWithAngusMail(String val, String hash) {
        Assert.assertEquals(hex(MSCHAPHelper.hashNtPasswordHash(bytes(val))), hash);
        Assert.assertEquals(MSCHAPHelper.hashNtPasswordHash(bytes(val)), bytes(hash));
    }

    @DataProvider(name = "hashNtPasswordHash", parallel = true)
    public static Object[][] hashNtPasswordHashDataProvider() {
        return new Object[][]{
                // 16 input bytes -> MD4 hash (all hex encoded)
                {"00000000000000000000000000000000", "487c3f5e2b0d6a79324ecdbe9c15166f"}, {
                    "ff000000000000000000000000000000", "dc361cd003791fc58d14ea74eb428c7e"}, {
                    "ffffffff000000000000000000000000", "2c981aa4baba60f1b647b7e9043c7d8b"}, {
                    "ffffffffffffffff0000000000000000", "170d8819a90e18874781787f2b3a9b64"}, {
                    "ffffffffffffffff00000000ffffffff", "19a35a5e43a3cc06edd0488ddae15b3f"}, {
                    "ffffffffffffffffffffffff00000000", "bb5a8e95faa96f7bf0079659138324a9"}, {
                    "ffffffffffffffffffffffffffffffff", "40f514675c1f016e351b200f72ff6589"}, {
                    "9c8e8d2d874a94637b3a540e7fe5ab88", "f1d2f37432982d40b635f36749055950"}, {
                    "9e1a3f8a0d7260c7313c092508db837c", "4dcd16f51cc4aea1b1655dc3ff8fd7a1"}, {
                    "0b5c853fdbc96c7a98e38cce3585e640", "56fd4b139f7e4611889c79f64637e006"}, {
                    "97d7cde3fa185c34cb3aa69846aa9e1e", "db68c5d1d7d9b190b87c71e38fa5e4ad"}, {
                    "b3c339609f4c58587a941c19c44e9d7b", "774aee87d25b7a51c3d07b5d359c60f6"}, {
                    "db4d3419a3df3a7355986c00088e909e", "35e30f6bec8a4205f8c8f0641a1de67a"}, {
                    "ff4ceadda0dfbfb9e04ea1f713f3803e", "ed0d1e0e74eae09bc2a5a5801204d996"}, {
                    "738bf1ad83f3f5da335190d4ffdb3bbb", "c96bb1c276e01453959c698898efb0e6"}, {
                    "d85bc4a5e4ec526d85165c1cebde70b4", "80f280c10dea132d180fc08161a1a8d5"}, {
                    "5022dd405b2021f04e9e6ea48bcd055f", "f6db06dd1e5c1ec6b18cd0d2ebbe13a8"}, {
                    "0bf6d1447905a41a1d044476c04033c4", "bfae182ff9184618abcd38ca455c2ab5"}, {
                    "e9f383a637cd56fad7047d6d2fa51ce0", "6e78a3cc6ab6a6bed1a2433c3d236e35"}, {
                    "f42620d9645f8289b8fc3064190b8307", "dd57fca501e75ee92eb5bc736b804497"}, {
                    "39a97d6bea05845c5713f09b60375a4b", "a685e7aea72f7b8f979622ba501d015b"}, {
                    "49a219ec5c78d116f16f382b392b0b62", "e2cce1c477a7d88ad2c74784e19dad98"}, {
                    "f784cf08189ab2fd197788c3d0bec017", "efdbe4a751d3a250c134dc0c94735786"}, {
                    "775a9582caa56c352ce1fd6dc29a287a", "e53de293c1dbc8c62d426308519f2faf"}, {
                    "d369c5ad5d6c7b2243f0331f79e64a5f", "d258cf5dd0d5bbcecf3af83718b19cc1"}, {
                    "569ec2db8af296ad55397d112fa8947b", "38eafdcfc54770274f02b6288f54aeac"}, {
                    "9298c5a810f60eefbdbe4b6bf3789a33", "6792c4fd597223b60c81e28c05f5eca2"}, {
                    "ea751e3db8defd7bf9db4a9a79dee99e", "dbee08ed8fd58e4788eec8e5b74bb647"}, {
                    "6b493bca05db0bb639be68c8c0d7f3ac", "ac51753d1b7de6940eac454895295b8b"}, {
                    "081d4a53bf9058bb283f63f125403aea", "ca6933f5c6fc9f831b7dd8c1c9224880"}, {
                    "5452fd8f1b7b8aa926696cbc0fd46e62", "03fafde70b63c11044488d7d09eb90dc"}, {
                    "2e83cde9365c69100e75f6dc0a1ba3d6", "047e0e1788a7535ac632670d38865e69"}};
    }

    @Test(dataProvider = "ntPasswordHash")
    public void testNtPasswordHashWithAngusMail(String val, String hash) {
        Assert.assertEquals(hex(MSCHAPHelper.ntPasswordHash(bytes(val))), hash);
        Assert.assertEquals(MSCHAPHelper.ntPasswordHash(bytes(val)), bytes(hash));
    }

    @DataProvider(name = "ntPasswordHash", parallel = true)
    public static Object[][] ntPasswordHashDataProvider() {
        return new Object[][]{
                // variable length input bytes -> MD4 hash (all hex encoded)
                {"", "31d6cfe0d16ae931b73c59d7e0c089c0"}, {"ff",
                "da8b893873fb6b73f8e7a8894c471620"}, {"ffffffff",
                "fb29921f4cf51b7d4a1146ff89144827"}, {"ffffffffffffffff",
                "3961e2c02e02576401e63fcf53fbe37d"}, {"ffffffffffffffffffffffff",
                "4be79fcfdf96de27bef26e5cd35d8074"}, {"ffffffffffffffffffffffffffffffff",
                "049143ef3f522b1f573d09722d7989f1"}, {
                    "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",
                "709f0203856cec246ce44f34e8e035b4"}, {
                    "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "1fae78558c5f402eba7ea28ebff6cc2c"}, {"587a024902ed30abc85ed1a8b15834", "e8c9467103ef6b05efd1599552f1c580"}, {"16d7b4af6f3bc20dabdd044f8fbeccff3bf39aa78b584642fbec345ea34fe3186033c6703a3ba5c69086a4cf4b2ca2899b165118d307", "2e45b070b1a6beef76077bcdcc8288e7"}, {"d9c5d51fe0370e3c6ae3b6c3c525e59a2a0b3270ca54b175c2e6bb70b4b349f33b3d26e32ea96e4ad7fe16e99d0d0826b5acd631f8e6d6467c", "c6ed6ee10a0a9b9af57b0e4dcc12e898"}, {"094717", "0983a36cd0d1567cb4cf8a0f06079f69"}, {"62191f732dd2b4a60237088ab88019aacb74aaa4b3", "08b3842fde22ad5bc608a31fbdb81f9f"}, {"527ef4c4329a5b0bdb1a5b68e67cd050", "3336bedefa98be3ab5ebf0e3f5e1a963"}, {"4b4610db59f430b896de58b45bcae0055de5736be9019ab181001a473d71a27af6bb429d91646e5df9116cbb198252c8", "8f25b8bb87f4b3210d16d190d9bb5593"}, {"a9700eb0044047ce19a89a4a3c57c00dd0ceda6dbc05d331b213db7ee57f8d621c4a0711847f623288bdde8e4da8", "fc71bb0d0bec402db8ea2a46cb9274cb"}, {"e973d879a14664e2cc81037e86d3c1bb46411f56a274e46e16ca81", "aca27e96c6a7b2adfda7d70e9a53ff26"}, {"82484e85221c05f7ae8b98", "7f4b8625872d0be251fa40db97e3970a"}, {"9a4e2cb870c626e14ef8a75971accd", "93134dad9c7abe33749717a8e3b3996a"}, {"0ebff68b35574f70624eafb5cacbab74b3deb1dc50d8d6b52131d393b00865eca1d26b5a8b0250515da82be7", "afed6b2afb409be4e8350f3c31eea3ad"}, {"0ef2761bf5", "ee2c5654bcd38c9664b3d4b12d2e86f7"}, {"89c5085d4bb312f4fa8bff048df08f383b5d6ae5a83b51973d6aec3fd36e613455a1d463b92c3a7b08f7a3e24acca92f0167726f", "694d1dd6843b61e71e047836c96caa90"}, {"6b718df6de683b85580c9ab449b414998cb82b6b73d92cb9d3f89d7dbddcdb5f51e2dd87bb19bccd74cc72f112a2d4006e36f08b390aac830b9fb65adf30a1", "7d426b5300c0b093e4a5634edb4a2d08"}, {"7aae7c32d613f9fd379348cd3ef56d8dfde3c2906f81f87ec5b862f0ce96979b02640078382467806706fbb30882ad04d4334f23389b4c9949b1e4", "fd6af354aa4f3debdff8b820abdef716"}, {"fbe6985f15ff5eb162a5e55f322414127114039317180df566562d28277d47474c", "b52baee38bc9c50dc6d6c10b7d39f430"}, {"36947bed", "85613d6b3b69a58ae8f75259599da2a7"}, {"09f38d22fd1d13a8bfaf3df63bf6abb4e0aa9f13860483c37bf38b2151cd8d0b80e643acf8081986026ccb6a0bda468cbefa2bf8ce5c73a1", "eb5ecbc237fe11bc1ed8b7df64b943aa"}, {"e63ba608662b8f570b5a3f22860476499d53b37dfad57084ec6221b6fdabac7360ce48c3f7cb8486a9", "cdd7b3fa9720e0035ecee11c5624517e"}, {"5481bd3c0b20a1c0855b87e463f6fde17a4edca0caa9b924e64e92f98ee28a11885e9e06a6da64c5bab83a0270ba8b5838f18a4ba1820d6d892afea7db4e", "cfcddbe48e0672562ce31b3719229a49"}, {"3389c956952b8ba19ab0e5822e0afa8c818e3d88a625bb00d4704d8ccacf8e7b7dc9b9b00a", "b686b8d9d4fecaa181792699112be264"}, {"d9", "010ca0a06d1b5d8294553c54174f3b9c"}, {"65458fb163e373", "e86a8a5d50e5ae646389ead953ef2233"},};
    }

    private static String hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] bytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(
                    hex.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * The old BouncyCastle based test for {@link MSCHAPHelper#hashNtPasswordHash(byte[])} (needs
     * bcprov on classpath)
     */
    @Test(dataProvider = "hashNtPasswordHash", enabled = false)
    public void testHashNtPasswordHashWithBouncyCastle(String val, String hash) throws Exception {
        registerSecurityProviderBouncyCastle();
        Assert.assertEquals(hex(hashNtPasswordHashBouncyCastle(bytes(val))), hash, "");
        Assert.assertEquals(hashNtPasswordHashBouncyCastle(bytes(val)), bytes(hash), "");

    }

    /**
     * The old BouncyCastle based {@link MSCHAPHelper#hashNtPasswordHash(byte[])} implementation.
     */
    private static byte[] hashNtPasswordHashBouncyCastle(byte[] passwordHash) throws NoSuchAlgorithmException {
        byte[] passwordHashHash = new byte[16];
        MessageDigest md = MessageDigest.getInstance("MD4");
        md.update(passwordHash, 0, 16);
        System.arraycopy(md.digest(), 0, passwordHashHash, 0, 16);
        return passwordHashHash;
    }

    /**
     * The old BouncyCastle based test for {@link MSCHAPHelper#ntPasswordHash(byte[])} (needs
     * bcprov on classpath)
     */
    @Test(dataProvider = "ntPasswordHash", enabled = false)
    public void testNtPasswordHashWithBouncyCastle(String val, String hash) throws NoSuchAlgorithmException {
        registerSecurityProviderBouncyCastle();
        Assert.assertEquals(hex(ntPasswordHashBouncyCastle(bytes(val))), hash);
        Assert.assertEquals(ntPasswordHashBouncyCastle(bytes(val)), bytes(hash));
    }

    /**
     * The old BouncyCastle based {@link MSCHAPHelper#ntPasswordHash(byte[])} implementation.
     */
    public static byte[] ntPasswordHashBouncyCastle(byte[] password) throws NoSuchAlgorithmException {
        byte[] passwordHash = new byte[16];
        byte[] uniPassword = unicode(password);
        MessageDigest md = MessageDigest.getInstance("MD4");
        md.update(uniPassword, 0, uniPassword.length);
        System.arraycopy(md.digest(), 0, passwordHash, 0, 16);
        return passwordHash;
    }

    private static byte[] unicode(byte[] in) {
        byte[] b = new byte[in.length * 2];
        for (int i = 0; i < in.length; i++) {
            b[(2 * i)] = in[i];
        }
        return b;
    }

    /**
     * Just to generate some fixed test data.
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        final SecureRandom random = new SecureRandom();
        registerSecurityProviderBouncyCastle();

        byte[] random16Bytes = new byte[16];
        System.out.println("hashNtPasswordHash");
        for (int i = 0; i < 32; i++) {
            random.nextBytes(random16Bytes);
            System.out.println("{ \"" + hex(random16Bytes) + "\", \"" + hex(
                    hashNtPasswordHashBouncyCastle(random16Bytes)) + "\" },");
        }

        System.out.println("ntPasswordHash");
        for (int i = 0; i < 32; i++) {
            byte[] randomBytes = new byte[random.nextInt(1, 64)];
            random.nextBytes(randomBytes);
            System.out.println("{ \"" + hex(randomBytes) + "\", \"" + hex(
                    ntPasswordHashBouncyCastle(randomBytes)) + "\" },");
        }
    }

    private static synchronized void registerSecurityProviderBouncyCastle() {
        if (!bcInitialized) {
            try {
                Class<?> clazz = Class.forName(BOUNCY_CASTLE_PROVIDER_CLASS);
                Provider provider = (Provider) clazz.getDeclaredConstructor().newInstance();
                Security.addProvider(provider);
                bcInitialized = true;
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                     IllegalAccessException | NoSuchMethodException e) {
                Assert.fail("BouncyCastle security provider (bcprov) not on classpath.", e);
            }
        }
    }
}