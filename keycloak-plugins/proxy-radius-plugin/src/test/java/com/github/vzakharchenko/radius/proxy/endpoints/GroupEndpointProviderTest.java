package com.github.vzakharchenko.radius.proxy.endpoints;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.keycloak.models.GroupModel;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.util.RadiusEndpoint;

import java.util.*;
import java.util.stream.Stream;

import static com.github.vzakharchenko.radius.proxy.endpoints.GroupProxyEndpointProvider.RADIUS_GROUP_PROXY_ENDPOINT;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

public class GroupEndpointProviderTest extends AbstractRadiusTest {
    private GroupProxyEndpointProvider groupProxyEndpointProvider = new GroupProxyEndpointProvider();
    private Map<String, List<String>> map;

    @Mock
    private GroupModel groupModel;

    @BeforeMethod
    public void beforeMethods() {
        reset(groupModel);
        when(userModel.getGroupsStream()).thenAnswer(i -> Stream.of(groupModel));
        map = new HashMap<>();
        map.put("AccessRequestAddress", Collections.singletonList("127.0.0.1"));
        map.put("AccessRequestPort", Collections.singletonList("1814"));
        map.put("AccessRequestSecret", Collections.singletonList("secret"));
        when(groupModel.getAttributes()).thenReturn(map);
    }

    @Test
    public void testMethods() {
        groupProxyEndpointProvider.init(null);
        groupProxyEndpointProvider.postInit(null);
        groupProxyEndpointProvider.close();
        assertEquals(groupProxyEndpointProvider.create(session), groupProxyEndpointProvider);
        assertEquals(groupProxyEndpointProvider.create(session), groupProxyEndpointProvider);
        assertEquals(groupProxyEndpointProvider.getId(), RADIUS_GROUP_PROXY_ENDPOINT);
    }

    @Test
    public void getRadiusEndpointTest() {
        RadiusEndpoint radiusEndpoint = groupProxyEndpointProvider.getRadiusEndpoint(session, AccessRequest.class);
        assertNotNull(radiusEndpoint);
        assertEquals(radiusEndpoint.getSecret(), "secret");
        assertNotNull(radiusEndpoint.getAddress());
        assertEquals(radiusEndpoint.getAddress().getAddress().getHostAddress(), "127.0.0.1");
        assertEquals(radiusEndpoint.getAddress().getPort(), 1814);
    }

    @Test
    public void getRadiusEndpointTestAddressEmpty() {
        map.remove("AccessRequestAddress");
        RadiusEndpoint radiusEndpoint = groupProxyEndpointProvider.getRadiusEndpoint(session, AccessRequest.class);
        assertNull(radiusEndpoint);
    }

    @Test
    public void getRadiusEndpointTestPortEmpty() {
        map.remove("AccessRequestPort");
        RadiusEndpoint radiusEndpoint = groupProxyEndpointProvider.getRadiusEndpoint(session, AccessRequest.class);
        assertNull(radiusEndpoint);
    }

    @Test
    public void getRadiusEndpointTestSecretEmpty() {
        map.remove("AccessRequestSecret");
        RadiusEndpoint radiusEndpoint = groupProxyEndpointProvider.getRadiusEndpoint(session, AccessRequest.class);
        assertNull(radiusEndpoint);
    }
}
