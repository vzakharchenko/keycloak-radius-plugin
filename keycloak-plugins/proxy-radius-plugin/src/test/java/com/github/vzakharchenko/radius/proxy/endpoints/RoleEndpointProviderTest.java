package com.github.vzakharchenko.radius.proxy.endpoints;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.util.RadiusEndpoint;

import java.util.*;
import java.util.stream.Stream;

import static com.github.vzakharchenko.radius.proxy.endpoints.RoleEndpointProvider.RADIUS_ROLE_PROXY_ENDPOINT;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

public class RoleEndpointProviderTest extends AbstractRadiusTest {
    private RoleEndpointProvider roleEndpointProvider = new RoleEndpointProvider();
    private Map<String, List<String>> map;

    @BeforeMethod
    public void beforeMethods() {
        when(userModel.getRoleMappingsStream()).thenAnswer(i -> Stream.of(radiusRole));
        map = new HashMap<>();
        map.put("AccessRequestAddress", Collections.singletonList("127.0.0.1"));
        map.put("AccessRequestPort", Collections.singletonList("1813"));
        map.put("AccessRequestSecret", Collections.singletonList("secret"));
        when(radiusRole.getAttributes()).thenReturn(map);
    }

    @Test
    public void testMethods() {
        roleEndpointProvider.init(null);
        roleEndpointProvider.postInit(null);
        roleEndpointProvider.close();
        assertEquals(roleEndpointProvider.create(session), roleEndpointProvider);
        assertEquals(roleEndpointProvider.create(session), roleEndpointProvider);
        assertEquals(roleEndpointProvider.getId(), RADIUS_ROLE_PROXY_ENDPOINT);
    }

    @Test
    public void getRadiusEndpointTest() {
        RadiusEndpoint radiusEndpoint = roleEndpointProvider.getRadiusEndpoint(session, AccessRequest.class);
        assertNotNull(radiusEndpoint);
        assertEquals(radiusEndpoint.getSecret(), "secret");
        assertNotNull(radiusEndpoint.getAddress());
        assertEquals(radiusEndpoint.getAddress().getAddress().getHostAddress(), "127.0.0.1");
        assertEquals(radiusEndpoint.getAddress().getPort(), 1813);
    }

    @Test
    public void getRadiusEndpointTestAddressEmpty() {
        map.remove("AccessRequestAddress");
        RadiusEndpoint radiusEndpoint = roleEndpointProvider.getRadiusEndpoint(session, AccessRequest.class);
        assertNull(radiusEndpoint);
    }

    @Test
    public void getRadiusEndpointTestPortEmpty() {
        map.remove("AccessRequestPort");
        RadiusEndpoint radiusEndpoint = roleEndpointProvider.getRadiusEndpoint(session, AccessRequest.class);
        assertNull(radiusEndpoint);
    }

    @Test
    public void getRadiusEndpointTestSecretEmpty() {
        map.remove("AccessRequestSecret");
        RadiusEndpoint radiusEndpoint = roleEndpointProvider.getRadiusEndpoint(session, AccessRequest.class);
        assertNull(radiusEndpoint);
    }
}
