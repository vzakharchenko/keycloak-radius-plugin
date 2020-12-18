package com.github.vzakharchenko.radius.radius.handlers.otp;

import com.github.vzakharchenko.radius.models.OtpHolder;

import java.util.Map;
import java.util.Set;

public interface OtpPasswordInfo {
    void putAll(Map<String, OtpHolder> otpHolderMap);

    boolean isUseOtp();

    Map<String, OtpHolder> getOtpHolderMap();

    Set<String> getValidOtpPasswords(String originPassword);
    Set<String> addOtpPasswords(String originPassword);
}
