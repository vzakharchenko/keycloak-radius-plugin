package ua.zaskarius.keycloak.plugins.radius.password;

import org.keycloak.credential.CredentialInput;

public class RadiusCredentialInput implements CredentialInput {
    private final String password;
    private final String id;

    public RadiusCredentialInput(String password, String id) {
        this.password = password;
        this.id = id;
    }

    @Override
    public String getCredentialId() {
        return id;
    }

    @Override
    public String getType() {
        return RadiusCredentialModel.TYPE;
    }

    @Override
    public String getChallengeResponse() {
        return password;
    }
}
