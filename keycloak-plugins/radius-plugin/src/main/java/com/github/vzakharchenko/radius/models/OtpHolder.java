package com.github.vzakharchenko.radius.models;

import org.keycloak.credential.CredentialModel;

import java.util.List;

public class OtpHolder {
    private String subType;
    private CredentialModel credentialModel;
    private List<String> passwords;

    public OtpHolder(String subType, CredentialModel credentialModel,
                     List<String> passwords) {
        this.subType = subType;
        this.credentialModel = credentialModel;
        this.passwords = passwords;
    }

    public String getSubType() {
        return subType;
    }

    public CredentialModel getCredentialModel() {
        return credentialModel;
    }

    public List<String> getPasswords() {
        return passwords;
    }
}
