package ua.zaskarius.keycloak.plugins.radius.radius.handlers.attributes.conditionals;

import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.keycloak.models.GroupModel;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class GroupServiceTypeAttributeConditionalTest extends AbstractRadiusTest {
    GroupServiceTypeAttributeConditional groupServiceTypeAttributeConditional;

    @Mock
    private GroupModel groupModel;

    @BeforeMethod
    public void beforeMethods() {
        reset(groupModel);
        when(groupModel.getAttribute(AbstractServiceTypeAttributeConditional.SERVICE_TYPE)).thenReturn(Collections.singletonList("Login-service"));
        when(groupModel.getAttribute(AbstractServiceTypeAttributeConditional.PROTOCOL_TYPE)).thenReturn(Collections.singletonList("PAP"));
        groupServiceTypeAttributeConditional = new GroupServiceTypeAttributeConditional(session);
    }

    @Test
    public void testMethods() {
        List<String> serviceTypes = groupServiceTypeAttributeConditional
                .getServiceTypes(groupModel);
        assertNotNull(serviceTypes);
        assertEquals(serviceTypes.size(), 1);
        assertEquals(serviceTypes.get(0), "Login-service");
        List<String> protocolTypes = groupServiceTypeAttributeConditional.getProtocolTypes(groupModel);
        assertNotNull(protocolTypes);
        assertEquals(protocolTypes.size(), 1);
        assertEquals(protocolTypes.get(0), "PAP");

    }
}
