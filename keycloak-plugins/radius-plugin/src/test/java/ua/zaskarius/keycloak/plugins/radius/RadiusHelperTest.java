package ua.zaskarius.keycloak.plugins.radius;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.password.RadiusCredentialModel;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusDictionaryProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServerProvider;
import ua.zaskarius.keycloak.plugins.radius.providers.IRadiusServiceProvider;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.util.*;

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
        when(radiusServiceProvider1.attrbuteName()).thenReturn("n1");
        when(radiusServiceProvider2.attrbuteName()).thenReturn("n1");
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
    public void testGetProvider() {
        IRadiusServerProvider provider = RadiusHelper
                .getProvider(session);
        assertNotNull(provider);
    }

    @Test
    public void testIsUseRadius() {
        assertTrue(RadiusHelper.isUseRadius());
    }

    @Test
    public void testGeneratePassword() {
        assertNotNull(RadiusHelper.generatePassword());
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

    @Override
    protected List<? extends Object> resetMock() {
        return null;
    }
}
