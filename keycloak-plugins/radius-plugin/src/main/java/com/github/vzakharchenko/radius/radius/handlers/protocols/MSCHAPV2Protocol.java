package com.github.vzakharchenko.radius.radius.handlers.protocols;

import com.github.vzakharchenko.radius.models.Attribute26Holder;
import com.github.vzakharchenko.radius.radius.RadiusLibraryUtils;
import com.github.vzakharchenko.radius.radius.handlers.protocols.mschapv2.MSCHAPHelper;
import com.github.vzakharchenko.radius.radius.handlers.protocols.mschapv2.MSCHAPV2AuthenticatorUtils;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfo;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoGetter;
import org.apache.commons.codec.binary.Hex;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.attribute.VendorSpecificAttribute;
import org.tinyradius.dictionary.Dictionary;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;

import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.tinyradius.packet.PacketType.ACCESS_ACCEPT;


public class MSCHAPV2Protocol extends AbstractAuthProtocol {

    private static final Logger LOGGER = Logger
            .getLogger(MSCHAPV2Protocol.class);

    private static final int MS_CHAP_CHALLENGE = 11;
    private static final int MS_CHAP2_RESPONSE = 25;
    private static final int MS_CHAP_RESPONSE = 1;
    private static final int MICROSOFT = 311;

    private byte[] msChapChallenge;
    private byte[] msChap2Response;
    private byte[] ntResponse;
    private byte[] peerChallenge;
    private Dictionary dictionary;

    public MSCHAPV2Protocol(AccessRequest accessRequest, KeycloakSession session) {
        super(accessRequest, session);
        RadiusAttribute msChapChallengeAttribute = getChapChallengeAttribute(accessRequest);
        RadiusAttribute msChap2ResponseAttribute = getChap2ResponseAttribute(accessRequest);
        if (msChapChallengeAttribute != null && msChap2ResponseAttribute != null) {
            initMSCHAPV2Protocol(msChapChallengeAttribute, msChap2ResponseAttribute);
            this.dictionary = accessRequest.getDictionary();
        }

    }

    private RadiusAttribute getChapChallengeAttribute(AccessRequest accessRequest) {
        return accessRequest
                .getAttribute(MICROSOFT, MS_CHAP_CHALLENGE);
    }

    private RadiusAttribute getChap2ResponseAttribute(AccessRequest accessRequest) {
        RadiusAttribute attribute = accessRequest
                .getAttribute(MICROSOFT, MS_CHAP2_RESPONSE);
        if (attribute == null) {
            attribute = accessRequest.getAttribute(MICROSOFT, MS_CHAP_RESPONSE);
        }
        return attribute;
    }

    private void initMSCHAPV2Protocol(RadiusAttribute msChapChallengeAttribute,
                                      RadiusAttribute msChap2ResponseAttribute
    ) {
        this.msChap2Response = msChap2ResponseAttribute.getValue();
        this.msChapChallenge = msChapChallengeAttribute.getValue();
        if (msChapChallenge == null) {
            LOGGER.warn("CHAP challenge is null");
        } else if (msChap2Response == null) {
            LOGGER.warn("CHAP password must be 17 bytes");
        } else {
            msCHAPV2PeerChallenge();
            msCHAPV2Password();
        }
    }

    private boolean verifyMSChapV2Password(String plaintext) {

        if (msChapChallenge != null && msChap2Response != null) {
            if (plaintext == null || plaintext.isEmpty()) {
                LOGGER.warn("plaintext must not be empty");
            } else {
                try {
                    return MSCHAPHelper.verifyMSCHAPv2(
                            accessRequest.getUserName().getBytes(UTF_8),
                            plaintext.getBytes(UTF_8), msChapChallenge, msChap2Response)
                            ||
                            MSCHAPHelper.verifyMSCHAPv2(
                                    RadiusLibraryUtils.getRealUserName(accessRequest.getUserName(),
                                            getRealm()).getBytes(UTF_8),
                                    plaintext.getBytes(UTF_8), msChapChallenge, msChap2Response);
                } catch (NoSuchAlgorithmException e) {
                    LOGGER.error("NoSuchAlgorithmException", e);
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean verifyProtocolPassword(String password) {
        return verifyMSChapV2Password(password);
    }

    @Override
    public void answer(RadiusPacket answer, IRadiusUserInfoGetter radiusUserInfoGetter) {
        if (answer.getType() == ACCESS_ACCEPT) {
            try {
                addMSCHAPV2Response(answer, radiusUserInfoGetter);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public ProtocolType getType() {
        return ProtocolType.MSCHAPV2;
    }

    private void msCHAPV2PeerChallenge() {
        int pcStart = 2;
        int pcLength = 16;
        peerChallenge = copyBytes(msChap2Response, pcStart, pcLength);
    }

    private void msCHAPV2Password() {
        int pStart = 26;
        int pLength = 24;

        ntResponse = copyBytes(msChap2Response, pStart, pLength);
    }

    private byte[] copyBytes(byte[] attributeData, int start, int length) {
        byte[] rv = new byte[length];
        for (int i = start, j = 0; i < (start + length); i++, j++) {
            rv[j] = attributeData[i];
        }

        return rv;
    }

    private void addMSCHAPV2Response(RadiusPacket responsePacket,
                                     byte[] password,
                                     String secret,
                                     VendorSpecificAttribute msVendor)
            throws NoSuchAlgorithmException {
        byte[] ntHashHash = MSCHAPV2AuthenticatorUtils.getPasswordHashHash(password);
        byte[] mppeSendKey = MSCHAPHelper.mppeCHAP2GenKeySend128(ntHashHash, ntResponse);
        byte[] mppeRecvKey = MSCHAPHelper.mppeCHAP2GenKeyRecv128(ntHashHash, ntResponse);

        byte[] mppeSendKeyEncoded = MSCHAPHelper.generateEncryptedMPPEPassword(mppeSendKey,
                1024, secret.getBytes(UTF_8), accessRequest.getAuthenticator());
        byte[] mppeRecvKeyEncoded = MSCHAPHelper.generateEncryptedMPPEPassword(mppeRecvKey,
                1024, secret.getBytes(UTF_8), accessRequest.getAuthenticator());
        msVendor.addSubAttribute("MS-MPPE-Send-Key", Hex
                .encodeHexString(mppeSendKeyEncoded));
        msVendor.addSubAttribute("MS-MPPE-Recv-Key", Hex
                .encodeHexString(mppeRecvKeyEncoded));
        responsePacket.getAttributes().add(msVendor);
    }

    private void addMSCHAPV2Response(RadiusPacket responsePacket,
                                     IRadiusUserInfoGetter radiusUserInfoGetter)
            throws NoSuchAlgorithmException {
        IRadiusUserInfo radiusUserInfo = radiusUserInfoGetter.getRadiusUserInfo();
        byte[] password = radiusUserInfo.getActivePassword().getBytes(UTF_8);
        String secret = radiusUserInfo.getRadiusSecret();
        String successResponse = createMSCHAPV2Response(
                accessRequest.getUserName(), password, (byte) 0x01);
        VendorSpecificAttribute msVendor = new VendorSpecificAttribute(dictionary,
                VendorUtils.MS_VENDOR);
        RadiusAttribute radiusAttribute = RadiusLibraryUtils.get26Attribute(dictionary,
                Attribute26Holder.create().vendor(msVendor.getVendorId()
                ).attributeName("MS-CHAP2-Success").newType(
                        26).value(successResponse));
        msVendor.addSubAttribute(radiusAttribute);
        msVendor.addSubAttribute("MS-MPPE-Encryption-Policy", Hex
                .encodeHexString(new byte[]
                        {0x00, 0x00, 0x00, 0x01}));
        msVendor.addSubAttribute("MS-MPPE-Encryption-Type", Hex
                .encodeHexString(new byte[]
                        {0x00, 0x00, 0x00, 0x06}));
        addMSCHAPV2Response(responsePacket, password, secret, msVendor);
    }

    /**
     * Creates an MSCHAPV2 success response
     *
     * @param username
     * @param password
     * @param ident
     * @return
     */
    protected String createMSCHAPV2Response(String username,
                                            byte[] password,
                                            byte ident) {

        try {
            byte[] authResponse = MSCHAPV2AuthenticatorUtils.generateAuthenticatorResponse(
                    password,
                    ntResponse,
                    peerChallenge,
                    msChapChallenge,
                    username.getBytes(UTF_8)
            );

            return ident
                    + "S="
                    + Hex.encodeHexString(authResponse).toUpperCase(Locale.US);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }

    }
}
