package com.github.vzakharchenko.radius.password;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import com.github.vzakharchenko.radius.test.ModelBuilder;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.CredentialTypeMetadata;
import org.keycloak.credential.CredentialTypeMetadataContext;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.cache.UserCache;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class RadiusCredentialProviderTest extends AbstractRadiusTest {
    RadiusCredentialModel credentialModel = RadiusCredentialModel
            .createFromCredentialModel(ModelBuilder
                    .createCredentialModel());
    private RadiusCredentialProvider credentialProvider;
    private final UserCache userCache = mock(UserCache.class);

    private CredentialTypeMetadataContext metadataContext;

    @BeforeMethod
    public void beforeMethod() {
        reset(userCache);
        credentialProvider = new RadiusCredentialProvider(session);

        when(subjectCredentialManager.createStoredCredential(
                any(RadiusCredentialModel.class))).thenReturn(
                credentialModel);
        when(subjectCredentialManager.getStoredCredentialById(
                credentialModel.getId())).thenReturn(credentialModel);
        metadataContext = CredentialTypeMetadataContext.builder().user(userModel)
                .build(session);
        when(subjectCredentialManager.isConfiguredFor(anyString())).thenReturn(true);
    }


    @Test
    public void testCreateCredential() {
        when(subjectCredentialManager.getStoredCredentialById(
                credentialModel.getId())).thenReturn(null);
        CredentialModel credential = credentialProvider
                .createCredential(realmModel, userModel
                        , RadiusCredentialModel
                                .createFromCredentialModel(
                                        ModelBuilder.createCredentialModel()));
        assertEquals(credential.getSecretData(),
                "{\"password\":\"secret\"}");
        verify(subjectCredentialManager)
                .createStoredCredential(any(RadiusCredentialModel.class));
    }


    @Test
    public void testCreateCredentialEmptyDate() {
        when(session.userCache()).thenReturn(null);
        CredentialModel credential = credentialProvider
                .createCredential(realmModel, userModel
                        , RadiusCredentialModel
                                .createFromCredentialModel(ModelBuilder
                                        .createCredentialModel(null)));
        assertEquals(credential.getSecretData(),
                "{\"password\":\"secret\"}");
        verify(subjectCredentialManager)
                .updateStoredCredential(any(RadiusCredentialModel.class));
    }

    @Test
    public void testDeleteCred() {
        credentialProvider.deleteCredential(realmModel, userModel,
                "id");
        verify(subjectCredentialManager)
                .removeStoredCredentialById("id");
    }

    @Test
    public void testCredentialFromModel() {
        RadiusCredentialModel credentialFromModel = credentialProvider
                .getCredentialFromModel(
                        ModelBuilder.createCredentialModel());
        Assert.assertEquals(
                credentialFromModel.getSecret().getPassword(),
                "secret");
    }

    @Test
    public void testGetCredentialTypeMetadata() {
        CredentialTypeMetadata credentialTypeMetadata = credentialProvider
                .getCredentialTypeMetadata(metadataContext);
        assertNotNull(credentialTypeMetadata);
    }

    @Test
    public void testGetCredentialTypeMetadata2() {
        when(subjectCredentialManager.isConfiguredFor(anyString())).thenReturn(false);
        CredentialTypeMetadata credentialTypeMetadata = credentialProvider
                .getCredentialTypeMetadata(metadataContext);
        assertNotNull(credentialTypeMetadata);
    }

    @Test
    public void isValidTest() {
        assertTrue(credentialProvider.isValid(realmModel, userModel,
                new RadiusCredentialInput("secret"
                        , credentialModel.getId())));
    }

    @Test
    public void isNotValidTest() {
        assertFalse(credentialProvider.isValid(realmModel, userModel,
                new RadiusCredentialInput("111"
                        , credentialModel.getId())));
        assertFalse(credentialProvider.isValid(realmModel, userModel,
                UserCredentialModel.password("secret")));

        when(subjectCredentialManager
                .getStoredCredentialById(credentialModel
                        .getId())).thenReturn(ModelBuilder
                .createCredentialModel(123L,
                        "{\"password\":\"\"}"));
        assertFalse(credentialProvider.isValid(realmModel, userModel,
                UserCredentialModel.password("secret")));

        when(subjectCredentialManager
                .getStoredCredentialById(credentialModel
                        .getId())).thenReturn(ModelBuilder
                .createCredentialModel(123L, null));
        assertFalse(credentialProvider.isValid(realmModel, userModel,
                UserCredentialModel.password("secret")));

        when(subjectCredentialManager
                .getStoredCredentialById(credentialModel
                        .getId())).thenReturn(null);
        assertFalse(credentialProvider.isValid(realmModel, userModel,
                UserCredentialModel.password("secret")));
    }

    @Test
    public void createCredential() {
        assertTrue(credentialProvider.createCredential(realmModel,
                userModel, "secret", "id"));
    }

    @Test
    public void updateCredential() {
        assertTrue(credentialProvider.updateCredential(realmModel,
                userModel, new RadiusCredentialInput(
                        "secret", "id")));
    }

    @Test
    public void onCache() {
        CachedUserModel cachedUserModel = mock(CachedUserModel.class);
        when(cachedUserModel.credentialManager())
                .thenReturn(subjectCredentialManager);
        ConcurrentHashMap value = new ConcurrentHashMap();
        when(cachedUserModel.getCachedWith()).thenReturn(value);
        when(subjectCredentialManager
                .getStoredCredentialsByTypeStream(
                        credentialProvider.getType()))
                .thenReturn(Stream.of(credentialModel));
        credentialProvider.onCache(realmModel,
                cachedUserModel, userModel);
        assertTrue(!value.isEmpty());
    }

    @Test
    public void onCache2() {
        CachedUserModel cachedUserModel = mock(CachedUserModel.class);
        when(cachedUserModel.credentialManager())
                .thenReturn(subjectCredentialManager);
        ConcurrentHashMap value = new ConcurrentHashMap();
        when(cachedUserModel.getCachedWith()).thenReturn(value);
        when(subjectCredentialManager
                .getStoredCredentialsByTypeStream(
                        credentialProvider.getType()))
                .thenReturn(new ArrayList<CredentialModel>().stream());
        credentialProvider.onCache(realmModel, cachedUserModel, userModel);
        assertTrue(!value.isEmpty());
    }

    @Test
    public void onCacheEmpty() {
        CachedUserModel cachedUserModel = mock(CachedUserModel.class);
        when(cachedUserModel.credentialManager())
                .thenReturn(subjectCredentialManager);
        ConcurrentHashMap value = new ConcurrentHashMap();
        when(cachedUserModel.getCachedWith()).thenReturn(value);
        when(subjectCredentialManager
                .getStoredCredentialsByTypeStream(credentialProvider.getType()))
                .thenReturn(new ArrayList<CredentialModel>().stream());
        credentialProvider.onCache(realmModel, cachedUserModel, userModel);
        assertFalse(value.isEmpty());
    }

    @Test
    public void ongGetPassword() {
        CachedUserModel cachedUserModel = mock(CachedUserModel.class);
        when(cachedUserModel.credentialManager())
                .thenReturn(subjectCredentialManager);
        when(cachedUserModel.isMarkedForEviction()).thenReturn(false);
        ConcurrentHashMap value = new ConcurrentHashMap();
        value.put(RadiusCredentialProvider.PASSWORD_CACHE_KEY,
                Arrays.asList(credentialModel));
        when(cachedUserModel.getCachedWith()).thenReturn(value);
        when(subjectCredentialManager.getStoredCredentialsByTypeStream(
                credentialProvider.getType()))
                .thenReturn(new ArrayList<CredentialModel>().stream());
        assertNotNull(credentialProvider.getPassword(realmModel, cachedUserModel));
    }


    @Test
    public void ongGetPassword2() {
        CachedUserModel cachedUserModel = mock(CachedUserModel.class);
        when(cachedUserModel.credentialManager())
                .thenReturn(subjectCredentialManager);
        when(cachedUserModel.isMarkedForEviction()).thenReturn(true);
        when(subjectCredentialManager
                .getStoredCredentialsByTypeStream(
                        credentialProvider.getType()))
                .thenReturn(Stream.of(credentialModel));
        assertNotNull(credentialProvider
                .getPassword(realmModel, cachedUserModel));
    }


    @Test
    public void ongGetPasswordEmpty() {
        CachedUserModel cachedUserModel = mock(CachedUserModel.class);
        when(cachedUserModel.credentialManager())
                .thenReturn(subjectCredentialManager);
        when(cachedUserModel.isMarkedForEviction()).thenReturn(true);
        when(subjectCredentialManager
                .getStoredCredentialsByTypeStream(credentialProvider.getType()))
                .thenReturn(new ArrayList<CredentialModel>().stream());
        assertNull(credentialProvider
                .getPassword(realmModel, cachedUserModel));
    }

    @Test
    public void ongGetPasswordNull() {
        CachedUserModel cachedUserModel = mock(CachedUserModel.class);
        when(cachedUserModel.credentialManager())
                .thenReturn(subjectCredentialManager);
        when(cachedUserModel.isMarkedForEviction()).thenReturn(true);
        when(subjectCredentialManager
                .getStoredCredentialsByTypeStream(credentialProvider.getType()))
                .thenReturn(new ArrayList<CredentialModel>().stream());
        assertNull(credentialProvider
                .getPassword(realmModel, cachedUserModel));
    }

    @Test
    public void testMethods() {
        assertEquals(credentialProvider.
                getCredentialStore(userModel), subjectCredentialManager);
        assertEquals(credentialProvider.getType(),
                RadiusCredentialModel.TYPE);
        credentialProvider
                .disableCredentialType(realmModel, userModel, "");
        assertEquals(credentialProvider
                .getDisableableCredentialTypes(realmModel, userModel).size(), 0);
        assertTrue(credentialProvider
                .supportsCredentialType(credentialProvider.getType()));
        assertTrue(credentialProvider.isConfiguredFor(realmModel, userModel,
                credentialProvider.getType()));
        assertFalse(credentialProvider
                .supportsCredentialType(PasswordCredentialModel.TYPE));
        assertFalse(credentialProvider
                .isConfiguredFor(realmModel, userModel,
                        PasswordCredentialModel.TYPE));
    }
}
