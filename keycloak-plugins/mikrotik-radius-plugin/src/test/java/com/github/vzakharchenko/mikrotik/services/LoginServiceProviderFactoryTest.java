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

public class LoginServiceProviderFactoryTest extends AbstractRadiusTest {
    private LoginServiceProviderFactory loginServiceProviderFactory =
            new LoginServiceProviderFactory();

    @BeforeMethod
    public void beforeMethods() {

    }

    @Test
    public void testMethods() {
        Assert.assertEquals(loginServiceProviderFactory.attributeName(), "mikrotik");
        Assert.assertEquals(loginServiceProviderFactory.getId(), LoginServiceProviderFactory.MIKROTIK_LOGIN_SERVICE);
        assertEquals(loginServiceProviderFactory.serviceName(), "login");
        loginServiceProviderFactory.close();
        assertNotNull(loginServiceProviderFactory.create(session));
        loginServiceProviderFactory.init(null);
        loginServiceProviderFactory.postInit(null);
    }

    @Test
    public void testAccessRequestPAP() {
        AccessRequest accessRequest = spy(new AccessRequest(realDictionary, 0, new byte[16]));
        when(accessRequest.getAuthProtocol()).thenReturn("PAP");
        RadiusAttribute loginAttribute = realDictionary.getAttributeTypeByName("Service-Type")
                .create(realDictionary, "01");
        when(accessRequest.getAttribute("Service-Type")).thenReturn(loginAttribute);
        assertFalse(loginServiceProviderFactory.checkService(accessRequest));
    }

    @Test
    public void testAccessRequestCHAP() {
        AccessRequest accessRequest = spy(new AccessRequest(realDictionary, 0, new byte[16]));
        when(accessRequest.getAuthProtocol()).thenReturn("CHAP");
        RadiusAttribute loginAttribute = realDictionary.getAttributeTypeByName("Service-Type")
                .create(realDictionary, "01");
        when(accessRequest.getAttribute("Service-Type")).thenReturn(loginAttribute);
        assertFalse(loginServiceProviderFactory.checkService(accessRequest));
    }

    @Test
    public void testAccessRequestNull() {
        AccessRequest accessRequest = spy(new AccessRequest(realDictionary, 0, new byte[16]));
        when(accessRequest.getAuthProtocol()).thenReturn("CHAP");
        when(accessRequest.getAttribute("Service-Type")).thenReturn(null);
        assertFalse(loginServiceProviderFactory.checkService(accessRequest));
    }

    @Test
    public void testAccessRequestMSCHAP() {
        AccessRequest accessRequest = spy(new AccessRequest(realDictionary, 0, new byte[16]));
        when(accessRequest.getAuthProtocol()).thenReturn("mschapv2");
        RadiusAttribute loginAttribute = realDictionary.getAttributeTypeByName("Service-Type")
                .create(realDictionary, "01");
        when(accessRequest.getAttribute("Service-Type")).thenReturn(loginAttribute);
        assertTrue(loginServiceProviderFactory.checkService(accessRequest));
    }
}
