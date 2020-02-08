package com.github.vzakharchenko.radius;

import com.github.vzakharchenko.radius.password.RadiusCredentialModel;
import com.github.vzakharchenko.radius.providers.IRadiusDictionaryProvider;
import com.github.vzakharchenko.radius.providers.IRadiusServiceProvider;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static com.github.vzakharchenko.radius.RadiusHelper.getRandomByte;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class RadiusHelperTest extends AbstractRadiusTest {
    @Mock
    private IRadiusServiceProvider radiusServiceProvider1;
    @Mock
    private IRadiusServiceProvider radiusServiceProvider2;

    @BeforeMethod
    public void beforeMethods() {
        reset(radiusServiceProvider1, radiusServiceProvider2);
        when(radiusServiceProvider1.attributeName()).thenReturn("n1");
        when(radiusServiceProvider2.attributeName()).thenReturn("n1");
    }

    @Test
    public void testGetRandomByte(){
        byte randomByte1 = getRandomByte();
        assertNotEquals(randomByte1,0);
        assertNotEquals(randomByte1,getRandomByte());
        assertNotEquals(getRandomByte(),getRandomByte());
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
        List<String> attributes = RadiusHelper.getRealmAttributes(session);
        assertEquals(attributes.size(), 1);
    }

    @Test
    public void testRealmAttributesNull() {
        RadiusHelper.setRealmAttributes(Collections.emptyList());
        List<String> attributes = RadiusHelper.getRealmAttributes(session);
        assertEquals(attributes.size(), 0);
    }

    @Test
    public void testRealmAttributesNotNull() {
        RadiusHelper.setRealmAttributes(new ArrayList<>());
        Set<IRadiusDictionaryProvider> providers = session
                .getAllProviders(IRadiusDictionaryProvider.class);
        IRadiusDictionaryProvider radiusDictionaryProvider = providers.iterator().next();
        when(radiusDictionaryProvider.getRealmAttributes()).thenReturn(Arrays.asList("r", "r3"));
        List<String> attributes = RadiusHelper.getRealmAttributes(session);
        assertEquals(attributes.size(), 2);
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
    public void test() {
    }

    @Override
    protected List<? extends Object> resetMock() {
        return null;
    }
}
