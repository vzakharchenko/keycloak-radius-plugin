package com.github.vzakharchenko.radius.coa;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.client.handler.ClientPacketCodec;
import org.tinyradius.packet.PacketEncoder;

import static org.mockito.Mockito.*;

public class CoAChannelInitializerTest extends AbstractRadiusTest {
    @Mock
    private DatagramChannel datagramChannel;
    @Mock
    private ChannelPipeline channelPipeline;

    @BeforeMethod
    public void beforeMethods() {
        reset(datagramChannel);
        reset(channelPipeline);
        when(datagramChannel.pipeline()).thenReturn(channelPipeline);
    }

    @Test
    public void coaTest() {
        CoAChannelInitializer coAChannelInitializer = new CoAChannelInitializer(new ClientPacketCodec(new PacketEncoder(realDictionary)));
        coAChannelInitializer.initChannel(datagramChannel);
        verify(channelPipeline).addLast(any(ChannelHandler.class), any(ChannelHandler.class));
    }
}
