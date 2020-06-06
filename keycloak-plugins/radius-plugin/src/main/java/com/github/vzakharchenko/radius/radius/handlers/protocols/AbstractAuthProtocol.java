package com.github.vzakharchenko.radius.radius.handlers.protocols;

import com.github.vzakharchenko.radius.RadiusHelper;
import com.github.vzakharchenko.radius.event.log.EventLoggerUtils;
import com.github.vzakharchenko.radius.models.OtpHolder;
import com.github.vzakharchenko.radius.providers.IRadiusAttributeProvider;
import com.github.vzakharchenko.radius.radius.handlers.attributes.KeycloakAttributesType;
import com.github.vzakharchenko.radius.radius.handlers.clientconnection.RadiusClientConnection;
import com.github.vzakharchenko.radius.radius.handlers.otp.IOtpPasswordFactory;
import com.github.vzakharchenko.radius.radius.handlers.otp.OTPPasswordFactory;
import com.github.vzakharchenko.radius.radius.handlers.session.KeycloakSessionUtils;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoGetter;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.credential.OTPCredentialModel;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.packet.RadiusPackets;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.tinyradius.packet.PacketType.ACCESS_REJECT;

public abstract class AbstractAuthProtocol implements AuthProtocol {
    protected final AccessRequest accessRequest;
    protected final KeycloakSession session;
    private IOtpPasswordFactory otpPasswordGetter;

    public AbstractAuthProtocol(AccessRequest accessRequest, KeycloakSession session) {
        this.accessRequest = accessRequest;
        this.session = session;
        this.otpPasswordGetter = new OTPPasswordFactory();
    }


    @Override
    public RealmModel getRealm() {
        return RadiusHelper.getRealm(session, accessRequest);
    }

    protected abstract void answer(RadiusPacket answer,
                                   IRadiusUserInfoGetter radiusUserInfoGetter);


    private void prepareAnswerAttributes(IRadiusAttributeProvider provider,
                                         KeycloakAttributesType attributesType,
                                         RadiusPacket answer) {
        provider
                .createKeycloakAttributes(accessRequest, session, attributesType).read()
                .ignoreUndefinedAttributes(answer
                        .getDictionary())
                .fillAnswer(answer);
    }

    @Override
    public final RadiusPacket prepareAnswer(RadiusPacket answer) {
        RadiusPacket resAnswer = answer;
        answer.addAttribute("Acct-Interim-Interval", "60");
        IRadiusUserInfoGetter radiusUserInfoGetter = KeycloakSessionUtils
                .getRadiusUserInfo(session);
        if (radiusUserInfoGetter != null) {
            Set<IRadiusAttributeProvider> providers = session
                    .getAllProviders(IRadiusAttributeProvider.class);
            for (IRadiusAttributeProvider provider : providers) {
                for (KeycloakAttributesType attributesType : KeycloakAttributesType.values()) {
                    prepareAnswerAttributes(provider, attributesType, answer);
                }
            }
            answer(answer, radiusUserInfoGetter);
            KeycloakSessionUtils.clearOneTimePassword(session);
            resAnswer = radiusUserInfoGetter.getRadiusUserInfo().isForceReject() ?
                    RadiusPackets.create(accessRequest.getDictionary(), ACCESS_REJECT,
                            accessRequest.getIdentifier()) : answer;
        }
        return resAnswer;
    }

    protected boolean isValidProtocol() {
        return true;
    }

    @Override
    public final boolean isValid(InetSocketAddress address) {
        boolean isValid = getRealm() != null && isValidProtocol();
        if (!isValid) {
            EventBuilder event = EventLoggerUtils
                    .createMasterEvent(session,
                            new RadiusClientConnection(address, accessRequest));
            event.event(EventType.LOGIN).detail(EventLoggerUtils.RADIUS_MESSAGE,
                    "Protocol " + getClass() + " is not valid.")
                    .error("Please set Realm name in radius configuration");
        }
        return isValid;
    }

    public abstract boolean verifyProtocolPassword(String password);


    @Override
    public final boolean verifyPassword(String password) {
        return verifyProtocolPassword(password) || verifyPassword0(password, true);
    }

    @Override
    public AccessRequest getAccessRequest() {
        return accessRequest.copy();
    }

    public boolean verifyProtocolPassword() {
        return false;
    }

    private String excludeOtp(String password, String otp, boolean checkPassword) {
        return checkPassword ? StringUtils.removeEnd(password, otp) : otp;
    }

    private boolean verifyPassword0(String originPassword, boolean checkPassword) {
        AtomicBoolean ret = new AtomicBoolean(false);
        Map<String, OtpHolder> otPs = otpPasswordGetter.getOTPs(session);
        otPs.values().forEach(otpHolder -> otpHolder
                .getPasswords().forEach(password -> {
                    String excludeOtp = excludeOtp(originPassword, password, checkPassword);
                    if (verifyProtocolPassword(excludeOtp)) {
                        markActivePassword(excludeOtp);
                        otpPasswordGetter.validOTP(session,
                                password,
                                otpHolder.getCredentialModel().getId(),
                                OTPCredentialModel.TYPE);
                        ret.set(true);
                    }
                }));
        return ret.get() || verifyProtocolPassword();
    }

    @Override
    public final boolean verifyPassword() {
        return verifyPassword0("", false);
    }


    protected void markActivePassword(String userPassword) {
        IRadiusUserInfoGetter radiusUserInfoGetter = KeycloakSessionUtils
                .getRadiusUserInfo(session);
        radiusUserInfoGetter.getBuilder().activePassword(userPassword);
    }

    @VisibleForTesting
    public void setOtpPasswordGetter(IOtpPasswordFactory otpPasswordGetter) {
        this.otpPasswordGetter = otpPasswordGetter;
    }
}
