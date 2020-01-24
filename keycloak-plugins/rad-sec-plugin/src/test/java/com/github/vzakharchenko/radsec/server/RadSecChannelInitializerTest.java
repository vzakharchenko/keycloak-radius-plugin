package com.github.vzakharchenko.radsec.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;

import java.util.Arrays;
import java.util.List;

public class RadSecChannelInitializerTest extends AbstractRadiusTest {

    @Mock
    private ChannelHandler channel1;
    @Mock
    private ChannelHandler channel2;
    @Mock
    private IRadSecServerProvider sslProvider;

    @BeforeMethod
    public void beforeMethods() {

    }

    @Test
    public void testRadSecChannelInitializer() throws Exception {
        RadSecChannelInitializer radSecChannelInitializer =
                new RadSecChannelInitializer(sslProvider, channel1, channel2, session);
        radSecChannelInitializer.initChannel(new NioSocketChannel());
    }

    @Override
    protected List<? extends Object> resetMock() {
        return Arrays.asList(channel1, channel2, sslProvider);
    }
}
