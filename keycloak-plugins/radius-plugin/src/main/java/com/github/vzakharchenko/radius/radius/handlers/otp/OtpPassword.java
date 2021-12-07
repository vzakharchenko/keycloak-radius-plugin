package com.github.vzakharchenko.radius.radius.handlers.otp;

import com.github.vzakharchenko.radius.client.RadiusLoginProtocolFactory;
import com.github.vzakharchenko.radius.models.OtpHolder;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.models.ClientModel;

import java.util.*;
import java.util.stream.Collectors;

public class OtpPassword implements OtpPasswordInfo {
    private final boolean requiredAction;
    private final boolean clientUseOTP;
    private final Map<String, OtpHolder> otpHolders = new HashMap<>();

    public OtpPassword(boolean requiredAction, ClientModel clientModel) {
        this.requiredAction = requiredAction;
        String attribute = clientModel.getAttribute(RadiusLoginProtocolFactory.OTP);
        this.clientUseOTP = attribute == null || BooleanUtils.toBoolean(attribute);
    }

    @Override
    public void putAll(Map<String, OtpHolder> otpHolderMap) {
        this.otpHolders.putAll(otpHolderMap);
    }

    @Override
    public boolean isUseOtp() {
        return clientUseOTP && (requiredAction || !otpHolders.isEmpty());
    }

    @Override
    public Map<String, OtpHolder> getOtpHolderMap() {
        return requiredAction ? Collections.EMPTY_MAP : Collections
                .unmodifiableMap(otpHolders);
    }


    private String excludeOtp(String password, String otp) {
        return StringUtils.removeEnd(password, otp);
    }

    private String includeOtp(String password, String otp) {
        return password + otp;
    }


    @Override
    public Set<String> getValidOtpPasswords(String originPassword, boolean otpWithoutPassword) {
        return otpPasswords(originPassword, true, otpWithoutPassword);
    }

    @Override
    public Set<String> addOtpPasswords(String originPassword, boolean otpWithoutPassword) {
        return otpPasswords(originPassword, false, otpWithoutPassword);
    }

    private void otpPasswords(Set<String> passwords, String originPassword, boolean exclude) {
        otpHolders.values().forEach(otpHolder -> passwords.addAll(otpHolder
                .getPasswords().stream()
                .map(password -> exclude ?
                        excludeOtp(originPassword, password) :
                        includeOtp(originPassword, password))
                .filter(password -> !StringUtils.isEmpty(password) &&
                        !Objects.equals(password, originPassword))
                .collect(Collectors.toList())));
    }

    private void onlyOtp(Set<String> passwords) {
        otpHolders.values().forEach(otpHolder ->
                passwords.addAll(otpHolder.getPasswords()));
    }

    private Set<String> otpPasswords(String originPassword,
                                     boolean exclude,
                                     boolean otpWithoutPassword) {
        Set<String> passwords = new HashSet<>();
        otpPasswords(passwords, originPassword, exclude);
        if (otpWithoutPassword) {
            onlyOtp(passwords);
        }
        return passwords;
    }
}
