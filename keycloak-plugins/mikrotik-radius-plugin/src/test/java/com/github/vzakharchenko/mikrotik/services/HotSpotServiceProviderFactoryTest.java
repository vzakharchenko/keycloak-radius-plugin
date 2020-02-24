package com.github.vzakharchenko.mikrotik.services;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.packet.AccessRequest;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class HotSpotServiceProviderFactoryTest extends AbstractRadiusTest {
    private HotSpotServiceProviderFactory hotSpotServiceProviderFactory =
            new HotSpotServiceProviderFactory();

    @BeforeMethod
    public void beforeMethods() {

    }

    @Test
    public void testMethods() {
        Assert.assertEquals(hotSpotServiceProviderFactory.attributeName(), "mikrotik");
        Assert.assertEquals(hotSpotServiceProviderFactory.getId(), HotSpotServiceProviderFactory.MIKROTIK_HOTSPOT_SERVICE);
        assertEquals(hotSpotServiceProviderFactory.serviceName(), "hotspot");
        hotSpotServiceProviderFactory.close();
        assertNotNull(hotSpotServiceProviderFactory.create(session));
        hotSpotServiceProviderFactory.init(null);
        hotSpotServiceProviderFactory.postInit(null);
    }

    @Test
    public void testAccessRequestPAP() {
        AccessRequest accessRequest = spy(new AccessRequest(realDictionary, 0, new byte[16]));
        when(accessRequest.getAuthProtocol()).thenReturn("PAP");
        RadiusAttribute loginAttribute = realDictionary.getAttributeTypeByName("Service-Type")
                .create(realDictionary, "01");
        when(accessRequest.getAttribute("Service-Type")).thenReturn(loginAttribute);
        assertTrue(hotSpotServiceProviderFactory.checkService(accessRequest));
    }

    @Test
    public void testAccessRequestCHAP() {
        AccessRequest accessRequest = spy(new AccessRequest(realDictionary, 0, new byte[16]));
        when(accessRequest.getAuthProtocol()).thenReturn("CHAP");
        RadiusAttribute loginAttribute = realDictionary.getAttributeTypeByName("Service-Type")
                .create(realDictionary, "01");
        when(accessRequest.getAttribute("Service-Type")).thenReturn(loginAttribute);
        assertTrue(hotSpotServiceProviderFactory.checkService(accessRequest));
    }

    @Test
    public void testAccessRequestNull() {
        AccessRequest accessRequest = spy(new AccessRequest(realDictionary, 0, new byte[16]));
        when(accessRequest.getAuthProtocol()).thenReturn("CHAP");
        when(accessRequest.getAttribute("Service-Type")).thenReturn(null);
        assertFalse(hotSpotServiceProviderFactory.checkService(accessRequest));
    }

    @Test
    public void testAccessRequestMSCHAP() {
        AccessRequest accessRequest = spy(new AccessRequest(realDictionary, 0, new byte[16]));
        when(accessRequest.getAuthProtocol()).thenReturn("mschapv2");
        RadiusAttribute loginAttribute = realDictionary.getAttributeTypeByName("Service-Type")
                .create(realDictionary, "01");
        when(accessRequest.getAttribute("Service-Type")).thenReturn(loginAttribute);
        assertFalse(hotSpotServiceProviderFactory.checkService(accessRequest));
    }
}
