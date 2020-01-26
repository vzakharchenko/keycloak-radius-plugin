package com.github.vzakharchenko.radsec.server;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.github.vzakharchenko.radius.models.RadSecSettings;
import com.github.vzakharchenko.radius.models.RadiusServerSettings;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class RadSecServerProviderFactoryTest extends AbstractRadiusTest {
    public RadSecServerProviderFactory radSecServerProviderFactory =
            new RadSecServerProviderFactory();

    @Test
    public void testMethods() {
        radSecServerProviderFactory.postInit(session, null);
        Assert.assertEquals(radSecServerProviderFactory.getId(), RadSecServerProviderFactory.RADSEC_PROVIDER);
    }

    @Test
    public void testCreateInstance() {
        RadiusServerSettings settings = new RadiusServerSettings();
        when(configuration.getRadiusSettings()).thenReturn(settings);
        RadSecSettings radSecSettings = new RadSecSettings();
        radSecSettings.setUseRadSec(false);
        radSecSettings.setnThreads(1);
        settings.setRadSecSettings(radSecSettings);
        radSecServerProviderFactory.createInstance(session);
    }

    @Test
    public void testCreateInstanceStpped() {
        radSecServerProviderFactory.createInstance(session);
    }


}
