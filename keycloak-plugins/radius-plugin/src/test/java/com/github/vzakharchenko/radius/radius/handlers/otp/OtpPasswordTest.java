package com.github.vzakharchenko.radius.radius.handlers.otp;

import com.github.vzakharchenko.radius.models.OtpHolder;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.keycloak.credential.CredentialModel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class OtpPasswordTest extends AbstractRadiusTest {

    private OtpPassword otpPassword;

    @BeforeMethod
    public void testBeforeMethods() {
        otpPassword = new OtpPassword(false);
    }

    @Test
    public void requiredAndEmpty() {
        otpPassword = new OtpPassword(true);
        Assert.assertTrue(otpPassword.isUseOtp());
    }

    @Test
    public void requiredAndNotEmpty() {
        otpPassword = new OtpPassword(true);
        HashMap<String, OtpHolder> otpHolderMap = new HashMap<>();
        otpHolderMap.put("1", new OtpHolder("1", new CredentialModel(), Arrays.asList("test")));
        otpPassword.putAll(otpHolderMap);
        Assert.assertTrue(otpPassword.isUseOtp());
    }

    @Test
    public void notRequiredAndEmpty() {
        otpPassword = new OtpPassword(false);
        Assert.assertFalse(otpPassword.isUseOtp());
    }

    @Test
    public void notRequiredAndNotEmpty() {
        otpPassword = new OtpPassword(false);
        HashMap<String, OtpHolder> otpHolderMap = new HashMap<>();
        otpHolderMap.put("1", new OtpHolder("1", new CredentialModel(), Arrays.asList("test")));
        otpPassword.putAll(otpHolderMap);
        Assert.assertTrue(otpPassword.isUseOtp());
    }

    @Test
    public void getValidOtpPasswordsTest() {
        HashMap<String, OtpHolder> otpHolderMap = new HashMap<>();
        otpHolderMap.put("1", new OtpHolder("1", new CredentialModel(), Arrays.asList("123")));
        otpPassword.putAll(otpHolderMap);
        Set<String> validOtpPasswords = otpPassword.getValidOtpPasswords("test123", false);
        Assert.assertNotNull(validOtpPasswords);
        Assert.assertEquals(validOtpPasswords.size(), 1);
    }

    @Test
    public void getValidOtpPasswordsNotValid() {
        HashMap<String, OtpHolder> otpHolderMap = new HashMap<>();
        otpHolderMap.put("1", new OtpHolder("1", new CredentialModel(), Arrays.asList("123")));
        otpPassword.putAll(otpHolderMap);
        Set<String> validOtpPasswords = otpPassword.getValidOtpPasswords("test1234", false);
        Assert.assertNotNull(validOtpPasswords);
        Assert.assertEquals(validOtpPasswords.size(), 0);
    }

    @Test
    public void otpPasswordsTest0() {
        HashMap<String, OtpHolder> otpHolderMap = new HashMap<>();
        otpHolderMap.put("1", new OtpHolder("1", new CredentialModel(), Arrays.asList("123")));
        otpPassword.putAll(otpHolderMap);
        Set<String> validOtpPasswords = otpPassword.addOtpPasswords("test123",true);
        Assert.assertNotNull(validOtpPasswords);
        Assert.assertEquals(validOtpPasswords.size(), 2);
    }
    @Test
    public void otpPasswordsTest() {
        HashMap<String, OtpHolder> otpHolderMap = new HashMap<>();
        otpHolderMap.put("1", new OtpHolder("1", new CredentialModel(), Arrays.asList("123")));
        otpPassword.putAll(otpHolderMap);
        Set<String> validOtpPasswords = otpPassword.addOtpPasswords("test123",false);
        Assert.assertNotNull(validOtpPasswords);
        Assert.assertEquals(validOtpPasswords.size(), 1);
    }

    @Test
    public void otpPasswordsNotValidTest() {
        HashMap<String, OtpHolder> otpHolderMap = new HashMap<>();
        otpPassword.putAll(otpHolderMap);
        Set<String> validOtpPasswords = otpPassword.addOtpPasswords("test1234", false);
        Assert.assertNotNull(validOtpPasswords);
        Assert.assertEquals(validOtpPasswords.size(), 0);
    }
}
