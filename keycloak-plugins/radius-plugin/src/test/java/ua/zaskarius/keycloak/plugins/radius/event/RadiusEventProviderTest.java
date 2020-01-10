package ua.zaskarius.keycloak.plugins.radius.event;

import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class RadiusEventProviderTest extends AbstractRadiusTest {
    private RadiusEventProvider radiusEventProvider;

    @BeforeMethod
    public void beforeMethod(){
        radiusEventProvider=new RadiusEventProvider(session);
    }

    @Test
    public void testMethods(){
        assertEquals(radiusEventProvider.getAdminEventActionMap().size(),2);
        assertEquals(radiusEventProvider.getEventActionMap().size(),0);
        radiusEventProvider.close();
    }


}
