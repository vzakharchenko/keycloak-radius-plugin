package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals;

import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.keycloak.models.RoleModel;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RoleServiceTypeAttributeConditionalTest extends AbstractRadiusTest {
    RoleServiceTypeAttributeConditional roleServiceTypeAttributeConditional;

    @Mock
    private RoleModel roleModel;

    @BeforeMethod
    public void beforeMethods() {
        reset(roleModel);
        when(roleModel.getAttribute(AbstractServiceTypeAttributeConditional.SERVICE_TYPE)).thenReturn(Collections.singletonList("Login-service"));
        when(roleModel.getAttribute(AbstractServiceTypeAttributeConditional.PROTOCOL_TYPE)).thenReturn(Collections.singletonList("PAP"));
        roleServiceTypeAttributeConditional = new RoleServiceTypeAttributeConditional(session);
    }

    @Test
    public void testMethods() {
        List<String> serviceTypes = roleServiceTypeAttributeConditional
                .getServiceTypes(roleModel);
        assertNotNull(serviceTypes);
        assertEquals(serviceTypes.size(), 1);
        assertEquals(serviceTypes.get(0), "Login-service");
        List<String> protocolTypes = roleServiceTypeAttributeConditional
                .getProtocolTypes(roleModel);
        assertNotNull(protocolTypes);
        assertEquals(protocolTypes.size(), 1);
        assertEquals(protocolTypes.get(0), "PAP");

    }
}
