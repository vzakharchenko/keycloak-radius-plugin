package com.github.vzakharchenko.radius.radius;

import com.github.vzakharchenko.radius.models.file.RadiusAccessModel;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.keycloak.authentication.authenticators.client.ClientIdAndSecretAuthenticator;
import org.keycloak.models.UserModel;
import org.testng.annotations.Test;
import org.tinyradius.packet.RadiusPacket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class RadiusLibraryUtilsTest extends AbstractRadiusTest {
    @Test
    public void testgetOrEmpty() {
        byte[] bytes = RadiusLibraryUtils.getOrEmpty(null, 16);
        assertEquals(bytes, new byte[16]);
        byte[] bytes1 = {1};
        bytes = RadiusLibraryUtils.getOrEmpty(bytes1, 1);
        assertEquals(bytes, bytes1);
    }


    @Test
    public void setUserNameTest() {
        RadiusPacket radiusPacket = new RadiusPacket(realDictionary, 1, 1);
        RadiusLibraryUtils.setUserName(radiusPacket, "test");
    }

    @Test
    public void writeValueAsString() {
        assertNotNull(RadiusLibraryUtils.writeValueAsString(new RadiusAccessModel()));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void writeValueAsStringException() {
        RadiusLibraryUtils.writeValueAsString(new RadiusAccessModel() {
            @Override
            public String getSharedSecret() {
                throw new IllegalStateException("test");
            }
        });
    }

    @Test
    public void getByUserName() {
        when(userProvider.getUserByEmail(realmModel, USER)).thenReturn(null);
        UserModel userModel = RadiusLibraryUtils.getUserModel(session, USER, realmModel);
        assertNotNull(userModel);
        UserModel userModel2 = RadiusLibraryUtils.getUserByUsername(session, USER, realmModel);
        assertNotNull(userModel2);
        UserModel userModel3 = RadiusLibraryUtils.getUserByEmail(session, USER, realmModel);
        assertNull(userModel3);
    }

    @Test
    public void getByEmail() {
        when(userProvider.getUserByUsername(realmModel, USER)).thenReturn(null);
        UserModel userModel = RadiusLibraryUtils.getUserModel(session, USER, realmModel);
        assertNotNull(userModel);
        UserModel userModel2 = RadiusLibraryUtils.getUserByUsername(session, USER, realmModel);
        assertNull(userModel2);
        UserModel userModel3 = RadiusLibraryUtils.getUserByEmail(session, USER, realmModel);
        assertNotNull(userModel3);
    }

    @Test
    public void getByServiceAccount() {
        when(userProvider.getUserByUsername(realmModel, USER)).thenReturn(null);
        when(userProvider.getUserByEmail(realmModel, USER)).thenReturn(null);
        when(userProvider.getServiceAccount(clientModel)).thenReturn(userModel);
        UserModel userModel = RadiusLibraryUtils
                .getUserModel(session, CLIENT_ID, realmModel);
        assertNotNull(userModel);
        UserModel userModel2 = RadiusLibraryUtils
                .getUserByUsername(session, CLIENT_ID, realmModel);
        assertNull(userModel2);
        UserModel userModel3 = RadiusLibraryUtils
                .getServiceAccount(session, CLIENT_ID, realmModel);
        assertNotNull(userModel3);
    }

    @Test
    public void getServiceAccountPassword1Null() {
        assertNull(RadiusLibraryUtils.getServiceAccountPassword(null, realmModel));
    }

    @Test
    public void getServiceAccountPassword2Null() {
        UserModel userModel = mock(UserModel.class);
        when(userModel.isEnabled()).thenReturn(false);
        assertNull(RadiusLibraryUtils.getServiceAccountPassword(userModel, realmModel));
    }

    @Test
    public void getServiceAccountPassword3Null() {
        when(userModel.getServiceAccountClientLink()).thenReturn(CLIENT_ID);
        when(realmModel.getClientById(CLIENT_ID)).thenReturn(clientModel);
        when(clientModel.getSecret()).thenReturn("test");
        when(clientModel.getClientAuthenticatorType()).thenReturn("123");
        assertNull(RadiusLibraryUtils.getServiceAccountPassword(userModel, realmModel));
    }

    @Test
    public void getServiceAccountPasswordSuccess() {
        when(userModel.getServiceAccountClientLink()).thenReturn(CLIENT_ID);
        when(realmModel.getClientById(CLIENT_ID)).thenReturn(clientModel);
        when(clientModel.getSecret()).thenReturn("test");
        when(clientModel.getClientAuthenticatorType())
                .thenReturn(ClientIdAndSecretAuthenticator.PROVIDER_ID);
        assertEquals(RadiusLibraryUtils
                .getServiceAccountPassword(userModel, realmModel), "test");
    }
}
