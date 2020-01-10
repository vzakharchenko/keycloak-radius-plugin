package ua.zaskarius.keycloak.plugins.radius.radius.provider;

import ua.zaskarius.keycloak.plugins.radius.event.RadiusEventListenerProviderFactory;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.keycloak.events.EventListenerProvider;
import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class MikrotikRadiusProviderTest extends AbstractRadiusTest {

    private RadiusRadiusProvider provider = new RadiusRadiusProvider();
    private Set<String> eventListeners = new HashSet<>();
    @Mock
    private EventListenerProvider eventListenerProvider;

    @BeforeMethod
    public void beforeMethod() {
        eventListeners.clear();
        eventListeners.add(RadiusEventListenerProviderFactory.RADIUS_EVENT_LISTENER);
        when(realmModel.getEventsListeners()).thenReturn(
                eventListeners);
    }

    @Override
    protected List<? extends Object> resetMock() {
        return Arrays.asList(eventListenerProvider);
    }

    @Test
    public void testMethods() {
        provider.close();
        provider.createIfNotExists(realmModel, userModel, "");
        provider.deleteUser(realmModel, "");
        Assert.assertEquals(provider.fieldName(), "preferred_username");
        Assert.assertEquals(provider.fieldPassword(), "s");
        Assert.assertNull(provider.getPassword(realmModel, userModel));
    }

    @Test
    public void testInitFalse() {
        assertFalse(provider.init(realmModel));
    }

    @Test
    public void testInitTrue() {
        eventListeners.clear();
        assertTrue(provider.init(realmModel));
    }
}
