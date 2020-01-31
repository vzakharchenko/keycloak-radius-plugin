package com.github.vzakharchenko.radsec.codec;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.codec.binary.Hex;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.packet.PacketEncoder;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.server.RequestCtx;
import org.tinyradius.server.ResponseCtx;
import org.tinyradius.server.SecretProvider;
import org.tinyradius.util.RadiusPacketException;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class RadSecCodecTest extends AbstractRadiusTest {
    public static final String RADIUS_PACKAGE = "01d800a2cb454b6f149bac108cf538a" +
            "750a4d7780606000000010106746573741a18000001370b12a6b9a6e4729bfc7ef2" +
            "8793a06696a6441a3a0000013719340000bcf33249480d4c1e170557f15731dafd0" +
            "000000000000000122f06cdf3d2eeacef5fe3400492f77b9d512d72dde064121f113" +
            "139322e3130302e3230302e323038200d4d61696e20526f757465721a0c00003a8c09" +
            "06776966690406c064c801";
    public static final String ENCODED_PACKAGE = "01d800144241f8975514d1a0ebcee4d3cdf95e88";

    private RadSecCodec radSecCodec;

    @Mock
    private ChannelHandlerContext context;

    @Mock
    private SecretProvider secretProvider;
    @Mock
    private Channel channel;

    private PacketEncoder packetEncoder;

    @BeforeMethod
    public void beforeMethods() {
        reset(secretProvider);
        reset(context);
        reset(channel);
        when(context.channel()).thenReturn(channel);
        when(channel.remoteAddress()).thenReturn(new InetSocketAddress(0));
        when(secretProvider.getSharedSecret(any())).thenReturn("test");
        packetEncoder = spy(new PacketEncoder(realDictionary));
        radSecCodec = new RadSecCodec(
                packetEncoder,
                secretProvider);
    }

    @Test
    public void testDecode() throws Exception {
        List<Object> objects = new ArrayList<>();
        radSecCodec.decode(context, Unpooled.copiedBuffer(Hex
                .decodeHex(RADIUS_PACKAGE.toCharArray())), objects);
        assertEquals(objects.size(), 1);
        RequestCtx requestCtx = (RequestCtx) objects.get(0);
        assertEquals(Hex.encodeHexString(requestCtx
                .getRequest().getAuthenticator()), "cb454b6f149bac108cf538a750a4d778");

    }

    @Test
    public void testDecodeFail() throws Exception {
        List<Object> objects = new ArrayList<>();
        radSecCodec.decode(context, Unpooled.copiedBuffer(Hex
                .decodeHex((RADIUS_PACKAGE + RADIUS_PACKAGE).toCharArray())), objects);
        assertEquals(objects.size(), 0);


    }


    @Test
    public void testEncode() throws Exception {
        List<Object> objects = new ArrayList<>();
        radSecCodec.decode(context, Unpooled.copiedBuffer(Hex
                .decodeHex(RADIUS_PACKAGE.toCharArray())), objects);
        assertEquals(objects.size(), 1);
        RequestCtx requestCtx = (RequestCtx) objects.get(0);
        ResponseCtx responseCtx = new ResponseCtx(
                requestCtx.getRequest(),
                requestCtx.getEndpoint(),
                new RadiusPacket(realDictionary, 1, requestCtx.getRequest().getIdentifier()));
        List<Object> responses = new ArrayList<>();
        radSecCodec.encode(context, responseCtx, responses);
        assertEquals(responses.size(), 1);
        ByteBuf byteBuf = (ByteBuf) responses.get(0);
        assertEquals(Hex.encodeHexString(byteBuf.array()),
                ENCODED_PACKAGE);
        RadiusPacket radiusPacket = packetEncoder.fromByteBuf(byteBuf);
        assertEquals(Hex.encodeHexString(radiusPacket.getAuthenticator()),
                "4241f8975514d1a0ebcee4d3cdf95e88");
    }

    @Test
    public void testEncodeFail() throws Exception {
        List<Object> objects = new ArrayList<>();
        radSecCodec.decode(context, Unpooled.copiedBuffer(Hex
                .decodeHex(RADIUS_PACKAGE.toCharArray())), objects);
        assertEquals(objects.size(), 1);
        RequestCtx requestCtx = (RequestCtx) objects.get(0);
        ResponseCtx responseCtx = new ResponseCtx(
                requestCtx.getRequest(),
                requestCtx.getEndpoint(),
                new RadiusPacket(realDictionary, 1, requestCtx.getRequest().getIdentifier()));
        List<Object> responses = new ArrayList<>();
        Mockito.doThrow(new RadiusPacketException("test")).when(packetEncoder).toByteBuf(any());
        radSecCodec.encode(context, responseCtx, responses);
        assertEquals(responses.size(), 0);
    }
}
