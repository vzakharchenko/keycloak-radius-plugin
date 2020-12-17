package com.github.vzakharchenko.radius;

import com.github.vzakharchenko.radius.client.RadiusLoginProtocolFactory;
import com.github.vzakharchenko.radius.password.RadiusCredentialModel;
import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProvider;
import com.github.vzakharchenko.radius.providers.IRadiusServiceProvider;
import com.github.vzakharchenko.radius.radius.dictionary.DictionaryLoader;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.apache.commons.codec.binary.Hex;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.attribute.AttributeType;
import org.tinyradius.dictionary.WritableDictionary;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.packet.RadiusPackets;

import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Stream;

import static com.github.vzakharchenko.radius.RadiusHelper.getRandomByte;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class RadiusHelperTest extends AbstractRadiusTest {
    @Mock
    private IRadiusServiceProvider radiusServiceProvider1;
    @Mock
    private IRadiusServiceProvider radiusServiceProvider2;

    @Mock
    private WritableDictionary dictionary;


    @BeforeMethod
    public void beforeMethods() {
        reset(radiusServiceProvider1, radiusServiceProvider2, dictionary, stream);
        when(radiusServiceProvider1.attributeName()).thenReturn("n1");
        when(radiusServiceProvider2.attributeName()).thenReturn("n1");
        when(dictionary.getAttributeTypeByName("realm-attribute"))
                .thenReturn(new AttributeType(1, "realm-attribute", "string"));
        when(dictionary.getAttributeTypeByName("r"))
                .thenReturn(new AttributeType(2, "r", "string"));
        when(dictionary.getAttributeTypeByName("r3"))
                .thenReturn(new AttributeType(3, "r3", "string"));
        DictionaryLoader.getInstance().setWritableDictionary(realDictionary);
    }

    @Test
    public void testGetRandomByte() {
        byte randomByte1 = getRandomByte();
        assertNotEquals(randomByte1, 0);
        assertNotEquals(randomByte1, getRandomByte());
        assertNotEquals(getRandomByte(), getRandomByte());
    }

    @Test
    public void testPassword() {
        String password = RadiusHelper.getPassword(session, realmModel, userModel);
        assertEquals(password, "secret");
    }

    @Test(expectedExceptions = IllegalStateException.class,
            expectedExceptionsMessageRegExp = "USER does not have radius password")
    public void testPasswordEmptyCredential() {
        when(userCredentialManager
                .getStoredCredentialsByType(realmModel, userModel,
                        RadiusCredentialModel.TYPE))
                .thenReturn(new ArrayList<>());
        assertNull(RadiusHelper.getPassword(session, realmModel, userModel));
    }


    @Test
    public void testCurrentPassword() {
        String password = RadiusHelper.getCurrentPassword(session, realmModel, userModel);
        assertEquals(password, "secret");
    }

    @Test
    public void testCurrentPasswordEmptyCredential() {
        when(userCredentialManager
                .getStoredCredentialsByType(realmModel, userModel,
                        RadiusCredentialModel.TYPE))
                .thenReturn(new ArrayList<>());
        assertNull(RadiusHelper.getCurrentPassword(session, realmModel, userModel));
    }

    @Test
    public void testIsUseRadius() {
        assertTrue(RadiusHelper.isUseRadius());
    }

    @Test
    public void testGeneratePassword() {
        String p1 = RadiusHelper.generatePassword();
        assertNotNull(p1);
        String p2 = RadiusHelper.generatePassword();
        assertNotEquals(p1, p2);
    }

    @Test
    public void testRealmAttributes() {
        RadiusHelper.setRealmAttributes(Collections.singletonList("realm-attribute"));
        RadiusPacket radiusPacket = RadiusPackets.create(dictionary, 1, 1);
        radiusPacket.addAttribute("realm-attribute", Hex.encodeHexString(REALM_RADIUS.getBytes(Charset.defaultCharset())));
        RealmModel realmModel = RadiusHelper.getRealm(session, radiusPacket);
        assertNotNull(realmModel);
        assertEquals(realmModel.getName(), REALM_RADIUS_NAME);
    }

    @Test
    public void testRealmAttributesNullWithDefaultRealm() {
        RadiusHelper.setRealmAttributes(Collections.emptyList());
        RadiusPacket radiusPacket = RadiusPackets.create(realDictionary, 1, 1);
        RealmModel realmModel = RadiusHelper.getRealm(session, radiusPacket);
        assertNotNull(realmModel);
        assertEquals(realmModel.getName(), REALM_RADIUS_NAME);
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "Found more than one Radius Realm \\(RadiusName, second_realm\\). If you expect to use the Default Realm, than you should use only one realm with radius client")
    public void testRealmAttributesNullWith2DefaultRealm() {
        RealmModel secondRealm = mock(RealmModel.class);
        when(secondRealm.getId()).thenReturn("second_realm");
        when(secondRealm.getName()).thenReturn("second_realm");
        ClientModel secondClientModel = mock(ClientModel.class);
        when(secondClientModel.getProtocol()).thenReturn(RadiusLoginProtocolFactory.RADIUS_PROTOCOL);
        when(secondRealm.getClients()).thenReturn(Arrays.asList(secondClientModel));
        when(realmProvider.getRealms()).thenReturn(Arrays.asList(realmModel, secondRealm));
        RadiusHelper.setRealmAttributes(Collections.emptyList());
        RadiusPacket radiusPacket = RadiusPackets.create(realDictionary, 1, 1);
        RadiusHelper.getRealm(session, radiusPacket);
        assertEquals(realmModel.getName(), REALM_RADIUS_NAME);
    }

    @Test
    public void testRealmAttributesNotNull() {
        RadiusHelper.setRealmAttributes(new ArrayList<>());
        Set<IRadiusDictionaryProvider> providers = session
                .getAllProviders(IRadiusDictionaryProvider.class);
        IRadiusDictionaryProvider radiusDictionaryProvider = providers.iterator().next();
        when(radiusDictionaryProvider.getRealmAttributes()).thenReturn(Arrays.asList("r", "r3"));
        RadiusPacket radiusPacket = RadiusPackets.create(dictionary, 1, 1);
        radiusPacket.addAttribute("realm-attribute", Hex.encodeHexString(REALM_RADIUS.getBytes(Charset.defaultCharset())));
        RealmModel realmModel = RadiusHelper.getRealm(session, radiusPacket);
        assertNotNull(realmModel);
    }

    @Test
    public void testGetServiceMapCache() {
        assertEquals(RadiusHelper.getServiceMap(session).size(), 1);
    }

    @Test
    public void testGetServiceMapEmpty() {
        RadiusHelper.getServiceMap0().clear();
        when(session
                .getAllProviders(IRadiusServiceProvider.class)).thenReturn(new HashSet<>(
                Arrays.asList(radiusServiceProvider1, radiusServiceProvider2)));
        assertEquals(RadiusHelper.getServiceMap(session).size(), 1);
        assertEquals(RadiusHelper.getServiceMap(session).get("n1").size(), 2);
    }

    @Test
    public void testGetRealm() {
        RadiusPacket radiusPacket = RadiusPackets.create(realDictionary, 1, 1);
        RadiusHelper.getRealm(session, radiusPacket);
    }

    @Override
    protected List<? extends Object> resetMock() {
        return null;
    }
}
