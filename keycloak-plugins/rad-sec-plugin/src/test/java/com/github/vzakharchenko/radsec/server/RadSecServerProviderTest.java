package com.github.vzakharchenko.radsec.server;

import com.github.vzakharchenko.radsec.providers.IRadiusRadSecHandlerProvider;
import io.netty.channel.Channel;
import org.mockito.Mock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.github.vzakharchenko.radius.models.RadSecSettings;
import com.github.vzakharchenko.radius.models.RadiusServerSettings;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;

import static org.mockito.Mockito.*;

public class RadSecServerProviderTest extends AbstractRadiusTest {
    private RadSecServerProvider radSecServerProvider;
    @Mock
    private Channel ch;

    @BeforeMethod
    public void beforeMethods() {
        reset(ch);
        RadiusServerSettings settings = new RadiusServerSettings();
        when(configuration.getRadiusSettings()).thenReturn(settings);
        RadSecSettings radSecSettings = new RadSecSettings();
        settings.setRadSecSettings(radSecSettings);
        radSecSettings.setUseRadSec(true);
        radSecSettings.setCert("src/test/resources/cert.crt");
        radSecSettings.setPrivKey("src/test/resources/cert.key");
    }

    @Test
    public void testRadsecChannel() {
        radSecServerProvider.radsecChannel(session);
        IRadiusRadSecHandlerProvider provider = session
                .getProvider(IRadiusRadSecHandlerProvider.class);
        verify(provider).getChannelHandler(session);
    }

    @Test
    public void testCreateHandler() {
        radSecServerProvider.createHandler(ch);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testCreateHandlerFailPublicKey1() {
        RadiusServerSettings settings = new RadiusServerSettings();
        when(configuration.getRadiusSettings()).thenReturn(settings);
        RadSecSettings radSecSettings = new RadSecSettings();
        settings.setRadSecSettings(radSecSettings);
        radSecSettings.setUseRadSec(true);
        radSecSettings.setCert("src/test/resources/public1.crt");
        radSecSettings.setPrivKey("src/test/resources/cert.key");
        radSecServerProvider.createHandler(ch);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testCreateHandlerFailPublicKey2() {
        RadiusServerSettings settings = new RadiusServerSettings();
        when(configuration.getRadiusSettings()).thenReturn(settings);
        RadSecSettings radSecSettings = new RadSecSettings();
        settings.setRadSecSettings(radSecSettings);
        radSecSettings.setUseRadSec(true);
        radSecSettings.setCert("src/test/resources/cert.key");
        radSecSettings.setPrivKey("src/test/resources/cert.key");
        radSecServerProvider.createHandler(ch);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testCreateHandlerFailPrivateKey1() {
        RadiusServerSettings settings = new RadiusServerSettings();
        when(configuration.getRadiusSettings()).thenReturn(settings);
        RadSecSettings radSecSettings = new RadSecSettings();
        settings.setRadSecSettings(radSecSettings);
        radSecSettings.setUseRadSec(true);
        radSecSettings.setCert("src/test/resources/cert.crt");
        radSecSettings.setPrivKey("src/test/resources/private.key");
        radSecServerProvider.createHandler(ch);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testCreateHandlerFailPrivateKey2() {
        RadiusServerSettings settings = new RadiusServerSettings();
        when(configuration.getRadiusSettings()).thenReturn(settings);
        RadSecSettings radSecSettings = new RadSecSettings();
        settings.setRadSecSettings(radSecSettings);
        radSecSettings.setUseRadSec(true);
        radSecSettings.setCert("src/test/resources/cert.crt");
        radSecSettings.setPrivKey("");
        radSecServerProvider.createHandler(ch);
    }

    @Test
    public void testStartServer() {
        radSecServerProvider = new RadSecServerProvider(session);
    }

    @Test
    public void testStartServer2() {
        radSecServerProvider = new RadSecServerProvider(session);
    }

    @Test
    public void startServerSkip() {
        RadiusServerSettings settings = new RadiusServerSettings();
        when(configuration.getRadiusSettings()).thenReturn(settings);
        RadSecSettings radSecSettings = new RadSecSettings();
        radSecSettings.setUseRadSec(false);
        settings.setRadSecSettings(radSecSettings);
        radSecServerProvider = new RadSecServerProvider(session);
    }

    @AfterMethod
    public void afterMethods() {
        if (radSecServerProvider != null) {
            radSecServerProvider.stop();
        }
    }
}
