package com.github.vzakharchenko.radius.models.file;


import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RadiusConfigModel {
    private String sharedSecret;
    private RadSecSettingsModel radsec;
    private CoASettingsModel coa;
    private int authPort = 1812;
    private int accountPort = 1813;
    private int numberThreads = 8;
    @Deprecated(since = "1.4.13")
    private boolean otp;
    private Set<String> otpWithoutPassword = Collections.emptySet();
    private String externalDictionary;
    private List<RadiusAccessModel> radiusIpAccess;
    private boolean useUdpRadius;

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public int getAuthPort() {
        return authPort;
    }

    public void setAuthPort(int authPort) {
        this.authPort = authPort;
    }

    public int getAccountPort() {
        return accountPort;
    }

    public void setAccountPort(int accountPort) {
        this.accountPort = accountPort;
    }

    public List<RadiusAccessModel> getRadiusIpAccess() {
        return radiusIpAccess;
    }

    public void setRadiusIpAccess(List<RadiusAccessModel> radiusIpAccess) {
        this.radiusIpAccess = radiusIpAccess;
    }

    public boolean isUseUdpRadius() {
        return useUdpRadius;
    }

    public void setUseUdpRadius(boolean useUdpRadius) {
        this.useUdpRadius = useUdpRadius;
    }

    public RadSecSettingsModel getRadsec() {
        return radsec;
    }

    public void setRadsec(RadSecSettingsModel radsec) {
        this.radsec = radsec;
    }

    public int getNumberThreads() {
        return numberThreads;
    }

    public void setNumberThreads(int numberThreads) {
        this.numberThreads = numberThreads;
    }

    public CoASettingsModel getCoa() {
        return coa;
    }

    public void setCoa(CoASettingsModel coa) {
        this.coa = coa;
    }

    @Deprecated(since = "1.4.13")
    public boolean isOtp() {
        return otp;
    }

    @Deprecated(since = "1.4.13")
    public void setOtp(boolean otp) {
        this.otp = otp;
    }

    public Set<String> getOtpWithoutPassword() {
        return otpWithoutPassword;
    }

    public void setOtpWithoutPassword(Set<String> otpWithoutPassword) {
        this.otpWithoutPassword = otpWithoutPassword != null ?
                Collections.unmodifiableSet(otpWithoutPassword) : Collections.emptySet();
    }

    public String getExternalDictionary() {
        return externalDictionary;
    }

    public void setExternalDictionary(String externalDictionary) {
        this.externalDictionary = externalDictionary;
    }
}
