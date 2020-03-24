package com.github.vzakharchenko.radius.password;

import com.github.vzakharchenko.radius.models.RadiusSecretData;
import org.keycloak.common.util.Time;
import org.keycloak.credential.*;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.cache.OnUserCache;
import org.keycloak.models.cache.UserCache;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.github.vzakharchenko.radius.password.UpdateRadiusPassword.RADIUS_UPDATE_PASSWORD;

public class RadiusCredentialProvider implements
        IRadiusCredentialProvider<RadiusCredentialModel>,
        CredentialInputUpdater,
        CredentialInputValidator,
        OnUserCache {

    public static final String PASSWORD_CACHE_KEY = RadiusCredentialProvider.class.getName()
            + "." + RadiusCredentialModel.TYPE;

    private final KeycloakSession session;

    public RadiusCredentialProvider(KeycloakSession session) {
        this.session = session;
    }

    protected UserCredentialStore getCredentialStore() {
        return session
                .userCredentialManager();
    }


    @Override
    public String getType() {
        return RadiusCredentialModel.TYPE;
    }

    @Override
    public CredentialModel createCredential(RealmModel realm,
                                            UserModel user,
                                            RadiusCredentialModel credentialModel) {
        if (credentialModel.getCreatedDate() == null) {
            credentialModel.setCreatedDate(Time.currentTimeMillis());
        }
        UserCredentialStore credentialStore = getCredentialStore();
        CredentialModel createdCredential = credentialStore
                .getStoredCredentialById(realm,
                        user, credentialModel.getId());
        if (createdCredential != null) {
            credentialStore.updateCredential(realm, user, credentialModel);
        } else {
            createdCredential = credentialStore
                    .createCredential(realm, user, credentialModel);
        }
        UserCache userCache = session.userCache();
        if (userCache != null) {
            userCache.evict(realm, user);
        }
        return createdCredential;
    }

    @Override
    public boolean deleteCredential(RealmModel realm, UserModel user, String credentialId) {
        return getCredentialStore().removeStoredCredential(realm, user, credentialId);
    }

    @Override
    public RadiusCredentialModel getCredentialFromModel(CredentialModel model) {
        return RadiusCredentialModel.createFromCredentialModel(model);
    }

    @Override
    public CredentialTypeMetadata getCredentialTypeMetadata(
            CredentialTypeMetadataContext metadataContext) {
        CredentialTypeMetadata.CredentialTypeMetadataBuilder metadataBuilder =
                CredentialTypeMetadata
                        .builder().type(this.getType()).category(
                        CredentialTypeMetadata.Category.BASIC_AUTHENTICATION)
                        .displayName("password-display-name")
                        .helpText("password-help-text")
                        .iconCssClass("kcAuthenticatorPasswordClass");
        UserModel user = metadataContext.getUser();
        if (user != null && this.session.userCredentialManager()
                .isConfiguredFor(this.session.getContext().getRealm(), user, this.getType())) {
            metadataBuilder.updateAction(RADIUS_UPDATE_PASSWORD);
        } else {
            metadataBuilder.createAction(RADIUS_UPDATE_PASSWORD);
        }
        return metadataBuilder.removeable(false).build(this.session);
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return credentialType.equals(getType());
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        if (!supportsCredentialType(credentialType)) {
            return false;
        }
        return !getCredentialStore()
                .getStoredCredentialsByType(realm, user, credentialType).isEmpty();
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user,
                           CredentialInput credentialInput) {
        if (Objects.equals(credentialInput.getType(), getType())) {
            CredentialModel credentialModel = getCredentialStore()
                    .getStoredCredentialById(realm, user,
                            credentialInput.getCredentialId());
            if (credentialModel != null) {
                RadiusCredentialModel credential = getCredentialFromModel(credentialModel);
                if (credential != null) {
                    RadiusSecretData secret = credential
                            .getSecret();
                    if (secret != null) {
                        return Objects.equals(secret
                                .getPassword(), credentialInput.getChallengeResponse());
                    }
                }
            }
        }
        return false;
    }

    public boolean createCredential(RealmModel realm,
                                    UserModel user, String password, String id) {
        RadiusCredentialModel credentialModel = RadiusCredentialModel
                .createFromValues(password, id);
        credentialModel.setCreatedDate(Time.currentTimeMillis());
        createCredential(realm, user, credentialModel);
        return true;
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user,
                                    CredentialInput input) {
        return createCredential(realm, user,
                input.getChallengeResponse(), input.getCredentialId());
    }

    @Override
    public void disableCredentialType(RealmModel realm,
                                      UserModel user,
                                      String credentialType) {

    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        return Collections.emptySet();
    }

    @Override
    public void onCache(RealmModel realm, CachedUserModel user, UserModel delegate) {
        List<CredentialModel> passwords = getCredentialStore()
                .getStoredCredentialsByType(realm, user, getType());
        if (passwords != null) {
            user.getCachedWith().put(PASSWORD_CACHE_KEY, passwords);
        }
    }

    @Override
    public RadiusCredentialModel getPassword(RealmModel realm, UserModel user) {
        List<CredentialModel> passwords = null;
        if (user instanceof CachedUserModel && !((CachedUserModel) user).isMarkedForEviction()) {
            CachedUserModel cached = (CachedUserModel) user;
            passwords = (List<CredentialModel>) cached.getCachedWith().get(PASSWORD_CACHE_KEY);

        }

        if (!(user instanceof CachedUserModel) || ((CachedUserModel) user)
                .isMarkedForEviction()) {
            passwords = getCredentialStore().getStoredCredentialsByType(realm, user, getType());
        }
        if (passwords == null || passwords.isEmpty()) {
            return null;
        }

        return RadiusCredentialModel.createFromCredentialModel(passwords.get(0));
    }
}
