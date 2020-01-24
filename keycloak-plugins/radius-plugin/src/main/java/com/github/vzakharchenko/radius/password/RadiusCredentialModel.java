package com.github.vzakharchenko.radius.password;

import com.github.vzakharchenko.radius.models.RadiusCredentialData;
import com.github.vzakharchenko.radius.models.RadiusSecretData;
import org.keycloak.credential.CredentialModel;
import org.keycloak.util.JsonSerialization;

import java.io.IOException;

public final class RadiusCredentialModel extends CredentialModel {
    public static final String TYPE = "radius-password";
    private final RadiusCredentialData credentialData;
    private final RadiusSecretData secretData;

    private RadiusCredentialModel(RadiusCredentialData credentialData,
                                  RadiusSecretData secretData) {
        super();
        this.credentialData = credentialData;
        this.secretData = secretData;
    }


    public static RadiusCredentialModel createFromCredentialModel(
            CredentialModel credentialModel) {
        try {
            RadiusCredentialData credentialData = JsonSerialization
                    .readValue(credentialModel.getCredentialData(), RadiusCredentialData.class);
            RadiusSecretData secretData = JsonSerialization
                    .readValue(credentialModel.getSecretData(), RadiusSecretData.class);

            RadiusCredentialModel radiusCredentialModel = new RadiusCredentialModel(
                    credentialData, secretData);
            radiusCredentialModel.setUserLabel(credentialModel.getUserLabel());
            radiusCredentialModel.setCreatedDate(credentialModel.getCreatedDate());
            radiusCredentialModel.setType(TYPE);
            radiusCredentialModel.setId(credentialModel.getId());
            radiusCredentialModel.setSecretData(credentialModel.getSecretData());
            radiusCredentialModel.setCredentialData(credentialModel.getCredentialData());
            return radiusCredentialModel;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    public static RadiusCredentialModel createFromValues(String password, String id) {
        RadiusCredentialData credentialData = new RadiusCredentialData();
        RadiusSecretData secretData = new RadiusSecretData(password);

        RadiusCredentialModel passwordCredentialModel = new RadiusCredentialModel(
                credentialData, secretData);

        try {
            passwordCredentialModel.setCredentialData(JsonSerialization
                    .writeValueAsString(credentialData));
            passwordCredentialModel.setSecretData(JsonSerialization
                    .writeValueAsString(secretData));
            passwordCredentialModel.setType(TYPE);
            passwordCredentialModel.setId(id);
            return passwordCredentialModel;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public RadiusCredentialData getCredential() {
        return credentialData;
    }

    public RadiusSecretData getSecret() {
        return secretData;
    }
}
