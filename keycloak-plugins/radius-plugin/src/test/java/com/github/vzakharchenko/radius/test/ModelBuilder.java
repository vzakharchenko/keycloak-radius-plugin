package com.github.vzakharchenko.radius.test;

import com.github.vzakharchenko.radius.models.CoASettings;
import com.github.vzakharchenko.radius.models.RadSecSettings;
import com.github.vzakharchenko.radius.models.RadiusServerSettings;
import com.github.vzakharchenko.radius.password.RadiusCredentialModel;
import org.keycloak.credential.CredentialModel;

import java.util.HashMap;

public class ModelBuilder {

    public static final String SHARED = "shared";
    public static final String IP = "123.123.123.123";

    public static RadiusServerSettings createRadiusServerSettings() {
        RadiusServerSettings radiusServerSettings = new RadiusServerSettings();
        radiusServerSettings.setSecret(SHARED);
        HashMap<String, String> accessList = new HashMap<>();
        accessList.put(IP, "ip_secret");
        radiusServerSettings.setAccessMap(accessList);
        radiusServerSettings.setUseUdpRadius(true);
        radiusServerSettings.setRadSecSettings(new RadSecSettings());
        radiusServerSettings.setAccountPort(9813);
        radiusServerSettings.setAuthPort(9812);
        CoASettings coASettings = new CoASettings();
        coASettings.setCoaPort(1700);
        coASettings.setUseCoAPackage(true);
        radiusServerSettings.setCoASettings(coASettings);
        return radiusServerSettings;
    }

    public static CredentialModel createCredentialModel() {
        return createCredentialModel(123L);
    }

    public static CredentialModel createCredentialModel(Long createDate) {
        return createCredentialModel(createDate,
                "{\"password\":\"secret\"}");
    }


    public static CredentialModel createCredentialModel(Long createDate,
                                                        String secret) {
        CredentialModel credentialModel = new CredentialModel();
        credentialModel.setCreatedDate(createDate);
        credentialModel.setType(RadiusCredentialModel.TYPE);
        credentialModel.setId("id");
        credentialModel.setSecretData(secret);
        credentialModel.setCredentialData("{}");
        return credentialModel;
    }
}
