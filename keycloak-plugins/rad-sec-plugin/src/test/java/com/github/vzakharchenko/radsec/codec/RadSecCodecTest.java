package com.github.vzakharchenko.radsec.codec;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.codec.DecoderException;
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
    public static final String ENCODED_PACKAGE = "01d80014e43e117af9d340ebd4ca0d7e0fb342f0";

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
        when(secretProvider.getSharedSecret(any())).thenReturn("radsec");
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
                .decodeHex(("cb454b6f149bac108cf538a750a4d77899" + "cb454b6f149bac108cf538a750a4d77899").toCharArray())), objects);
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
                "e43e117af9d340ebd4ca0d7e0fb342f0");
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

    @Test
    public void decodeIssue() throws DecoderException {
        List<Object> objects = new ArrayList<>();
        String issue = "04a400804674576841001df4c386f3be5ffaccd3060600000001011b7661737a616b6861726368656e6b6f40676d61696c2e636f6d1f113139322e3130302e3230302e3230382806000000032c0a3834303030303135200d4d61696e20526f757465722906000000001a1100003a8c090b536d617274486f6d650406d992fb7504a500809b2422441990962dd660fed567aa5ce7060600000001011b7661737a616b6861726368656e6b6f40676d61696c2e636f6d1f113139322e3130302e3230302e3230382806000000032c0a3834303030303134200d4d61696e20526f757465722906000000001a1100003a8c090b536d617274486f6d650406d992fb75";
        radSecCodec.decode(context, Unpooled.copiedBuffer(Hex
                .decodeHex(issue.toCharArray())), objects);
        assertEquals(objects.size(), 2);
    }

    @Test
    public void decodeIssue2() throws DecoderException {
        List<Object> objects = new ArrayList<>();
        String issue = "04c000808ca1556db05fc2f0112798e85f9334df060600000001011b7661737a616b6861726368656e6b6f40676d61696c2e636f6d1f113139322e3130302e3230302e3230382806000000012c0a3834303030303538200d4d61696e20526f757465722906000000001a1100003a8c090b536d617274486f6d650406d992fb7501c100bc7dd3a74138a86a10082398e1427c5876060600000001011b7661737a616b6861726368656e6b6f40676d61696c2e636f6d1a18000001370b129684ee4f918d8e196314a9579f7af41d1a3a0000013719340000b775581c7ac9869e1c758689c729c35d0000000000000000b3a6b62a0d65732d5e451a9630f8f5417b9b2e4820d7a4a31f113139322e3130302e3230302e323038200d4d61696e20526f757465721a1100003a8c090b536d617274486f6d650406d992fb75";
        radSecCodec.decode(context, Unpooled.copiedBuffer(Hex
                .decodeHex(issue.toCharArray())), objects);
        assertEquals(objects.size(), 1);
    }

    @Test
    public void decodeError() throws DecoderException {
        List<Object> objects = new ArrayList<>();
        String issue = "04c000808ca1556db05fc2f0112798e85f9334df060600000001011b7661737a616b6861726368656e6b6f40676d61696c2e636f6d1f113139322e3130302e3230302e3230382806000000012c0a3834303030303538200d4d61696e20526f757465722906000000001a1100003a8c090b536d617274486f6d650406d992fb7501c100bc7dd3a74138a86a10082398e1427c5876060600000001011b7661737a616b6861726368656e6b6f40676d61696c2e636f6d1a18000001370b129684ee4f918d8e196314a9579f7af41d1a3a0000013719340000b775581c7ac9869e1c758689c729c35d0000000000000000b3a6b62a0d65732d5e451a9630f8f5417b9b2e4820d7a4a31f113139322e3130302e3230302e323038200d4d61696e20526f757465721a1100003a8c090b536d617274486f6d650406d992fb75";
        radSecCodec.tryToFixPackage(context, Unpooled.copiedBuffer(Hex
                .decodeHex(issue.toCharArray())), null);
        assertEquals(objects.size(), 0);
    }


    @Test
    public void decodeIssueFail() throws DecoderException {
        List<Object> objects = new ArrayList<>();
        String issue = "04aa00f04674576841001df4c386f3be5ffaccd3060600000001011b7661737a616b6861726368656e6b6f40676d61696c2e636f6d1f113139322e3130302e3230302e3230382806000000032c0a3834303030303135200d4d61696e20526f757465722906000000001a1100003a8c090b536d617274486f6d650406d992fb7504a500809b2422441990962dd660fed567aa5ce7060600000001011b7661737a616b6861726368656e6b6f40676d61696c2e636f6d1f113139322e3130302e3230302e3230382806000000032c0a3834303030303134200d4d61696e20526f757465722906000000001a1100003a8c090b536d617274486f6d650406d992fb75";
        radSecCodec.decode(context, Unpooled.copiedBuffer(Hex
                .decodeHex(issue.toCharArray())), objects);
        assertEquals(objects.size(), 0);
    }

    @Test
    public void decode2() throws DecoderException {
        List<Object> objects = new ArrayList<>();
        String issue = "04a400804674576841001df4c386f3be5ffaccd3060600000001011b7661737a616b6861726368656e6b6f40676d61696c2e636f6d1f113139322e3130302e3230302e3230382806000000032c0a3834303030303135200d4d61696e20526f757465722906000000001a1100003a8c090b536d617274486f6d650406d992fb75";
        radSecCodec.decode(context, Unpooled.copiedBuffer(Hex
                .decodeHex(issue.toCharArray())), objects);
        assertEquals(objects.size(), 1);
    }

}
