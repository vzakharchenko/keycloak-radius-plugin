package com.github.vzakharchenko.radius.proxy.proxy;

import com.github.vzakharchenko.radius.proxy.client.IProxyRequestHandler;
import com.github.vzakharchenko.radius.proxy.client.IRadiusProxyClient;
import com.github.vzakharchenko.radius.proxy.client.RadiusProxyClientHelper;
import com.github.vzakharchenko.radius.proxy.providers.IRadiusProxyEndpointProvider;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.Promise;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.client.RadiusClient;
import org.tinyradius.client.timeout.TimeoutHandler;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.packet.RadiusPackets;
import org.tinyradius.util.RadiusEndpoint;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertNull;
import static org.tinyradius.packet.PacketType.ACCESS_ACCEPT;
import static org.tinyradius.packet.PacketType.ACCESS_REJECT;

public class RadiusComponentProxyTest extends AbstractRadiusTest {
    private RadiusComponentProxy radiusComponentProxy = new RadiusComponentProxy();
    private IRadiusProxyEndpointProvider provider;
    private AccessRequest accessRequest;
    private RadiusEndpoint radiusEndpoint;
    private RadiusClient radiusClient;


    private Bootstrap bootstrap;
    @Mock
    private TimeoutHandler timeoutHandler;
    @Mock
    private EventLoop eventLoop;

    @Mock
    private Promise promise;

    @Mock
    private ChannelHandler channelHandler;

    @Mock
    private ChannelFuture channelFuture;
    @Mock
    private Channel channel;


    private void initRadiusClient() {
        reset(timeoutHandler);
        reset(eventLoop);
        reset(promise);
        reset(channelHandler);
        reset(channelFuture);
        reset(channel);
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoop).channel(NioDatagramChannel.class);
        when(eventLoop.register(any(Channel.class))).thenReturn(channelFuture);
        when(eventLoop.newPromise()).thenReturn(promise);
        when(eventLoop.next()).thenReturn(eventLoop);
        when(promise.addListener(any())).thenReturn(promise);
        when(promise.syncUninterruptibly()).thenReturn(promise);
        when(channelFuture.channel()).thenReturn(channel);
        radiusClient = new RadiusClient(bootstrap,
                new InetSocketAddress(0), timeoutHandler, channelHandler);
    }

    @BeforeMethod
    public void beforeMethods() {
        initRadiusClient();
        radiusEndpoint = new RadiusEndpoint(new InetSocketAddress(0), "secret");
        accessRequest = new AccessRequest(realDictionary, 1, new byte[16]);
        provider = getProvider(IRadiusProxyEndpointProvider.class);
        //
        when(provider.getRadiusEndpoint(session, AccessRequest.class))
                .thenReturn(radiusEndpoint);

        IRadiusProxyClient radiusProxyClient = mock(IRadiusProxyClient.class);
        RadiusProxyClientHelper.setRadiusProxyClient(radiusProxyClient);
        when(radiusProxyClient.requestProxy(any(), any()))
                .thenAnswer((Answer<RadiusPacket>) invocationOnMock -> {
                    IProxyRequestHandler iProxyRequestHandler =
                            invocationOnMock.getArgument(1);
                    return iProxyRequestHandler.call(radiusClient);
                });
    }


    @Test
    public void getRadiusEndpointTest() {
        RadiusEndpoint radiusEndpoint = radiusComponentProxy
                .getRadiusEndpoint(session, accessRequest);
        assertNotNull(radiusEndpoint);
        assertEquals(radiusEndpoint, this.radiusEndpoint);
    }

    @Test
    public void getRadiusEndpointTestNull1() {
        when(provider.getRadiusEndpoint(session, AccessRequest.class))
                .thenReturn(null);
        RadiusEndpoint radiusEndpoint = radiusComponentProxy
                .getRadiusEndpoint(session, accessRequest);
        assertNull(radiusEndpoint);
    }

    @Test
    public void getRadiusEndpointTestNull2() {
        when(session.getAllProviders(IRadiusProxyEndpointProvider.class))
                .thenReturn(new HashSet<>(Collections.emptyList()));
        RadiusEndpoint radiusEndpoint = radiusComponentProxy
                .getRadiusEndpoint(session, accessRequest);
        assertNull(radiusEndpoint);
    }

    @Test
    public void answerHandlerTest() {
        RadiusPacket radiusPacket1 = RadiusPackets
                .create(realDictionary, ACCESS_ACCEPT,
                        1, new byte[16]);
        RadiusPacket radiusPacket2 =
                RadiusPackets.create(realDictionary,
                        ACCESS_REJECT, 1, new byte[16]);
        RadiusPacket radiusPacket = radiusComponentProxy
                .answerHandler(radiusPacket1, radiusPacket2);
        assertNotEquals(radiusPacket1, radiusPacket);
        assertNotEquals(radiusPacket2, radiusPacket);
        assertEquals(radiusPacket.getType(), ACCESS_REJECT);
    }

    @Test
    public void proxyTest() {
        RadiusPacket radiusPacket = RadiusPackets
                .create(realDictionary, ACCESS_ACCEPT,
                        1, new byte[16]);
        when(promise.getNow()).thenReturn(radiusPacket);
        RadiusPacket packet = radiusComponentProxy.proxy(session,
                accessRequest, radiusPacket);
        assertEquals(packet.getType(), ACCESS_ACCEPT);
    }

    @Test
    public void proxyTestFail() {
        RadiusPacket radiusPacket = RadiusPackets
                .create(realDictionary, ACCESS_ACCEPT,
                        1, new byte[16]);
        when(eventLoop.next()).thenThrow(new IllegalStateException("test"));
        RadiusPacket packet = radiusComponentProxy
                .proxy(session, accessRequest, radiusPacket);
        assertEquals(packet.getType(), ACCESS_REJECT);
    }

    @Test
    public void proxyTestWithout() {
        when(provider.getRadiusEndpoint(session, AccessRequest.class))
                .thenReturn(null);
        RadiusPacket radiusPacket = RadiusPackets
                .create(realDictionary, ACCESS_ACCEPT,
                        1, new byte[16]);
        when(eventLoop.next()).thenThrow(new IllegalStateException("test"));
        RadiusPacket packet = radiusComponentProxy
                .proxy(session, accessRequest, radiusPacket);
        assertEquals(packet, radiusPacket);
    }
}
