package com.github.vzakharchenko.radius.dm.jpa;

import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModel;
import com.github.vzakharchenko.radius.dm.test.AbstractJPATest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class DisconnectMessageManagerTest extends AbstractJPATest {
    private DisconnectMessageManager disconnectMessageManager;

    @BeforeMethod
    public void beforeMethods() {
        disconnectMessageManager = new DisconnectMessageManager(session);
    }

    @Test
    public void saveTest() {
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        disconnectMessageManager.saveRadiusSession(disconnectMessageModel);
        verify(entityManager).persist(disconnectMessageModel);
    }
    @Test
    public void endSessionTest() {
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        disconnectMessageManager.endSession(disconnectMessageModel);
        verify(entityManager).persist(disconnectMessageModel);
    }

    @Test
    public void getDisconnectMessageTestNull() {
        DisconnectMessageModel disconnectMessage = disconnectMessageManager
                .getDisconnectMessage("test", "sessionId");
        assertNull(disconnectMessage);
    }

    @Test
    public void getDisconnectMessageTest() {
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(disconnectMessageModel));
        DisconnectMessageModel disconnectMessage = disconnectMessageManager
                .getDisconnectMessage("test", "sessionId");
        assertEquals(disconnectMessage, disconnectMessageModel);
    }


}
