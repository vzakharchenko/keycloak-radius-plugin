package com.github.vzakharchenko.radius.radius.handlers.otp;

import com.github.vzakharchenko.radius.configuration.RadiusConfigHelper;
import com.github.vzakharchenko.radius.models.OtpHolder;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class OtpPassword implements OtpPasswordInfo {
    private final boolean requiredAction;
    private final Map<String, OtpHolder> otpHolders = new HashMap<>();

    public OtpPassword(boolean requiredAction) {
        this.requiredAction = requiredAction;
    }

    @Override
    public void putAll(Map<String, OtpHolder> otpHolderMap) {
        this.otpHolders.putAll(otpHolderMap);
    }

    @Override
    public boolean isUseOtp() {
        return requiredAction || !otpHolders.isEmpty();
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
    public Set<String> getValidOtpPasswords(String originPassword) {
        return otpPasswords(originPassword, true);
    }

    @Override
    public Set<String> addOtpPasswords(String originPassword) {
        return otpPasswords(originPassword, false);
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
        boolean allowedOtp = RadiusConfigHelper.getConfig().getRadiusSettings().isOtp();
        if (allowedOtp) {
            otpHolders.values().forEach(otpHolder ->
                    passwords.addAll(otpHolder.getPasswords()));
        }
    }

    private Set<String> otpPasswords(String originPassword, boolean exclude) {
        Set<String> passwords = new HashSet<>();
        otpPasswords(passwords, originPassword, exclude);
        onlyOtp(passwords);
        return passwords;
    }
}
