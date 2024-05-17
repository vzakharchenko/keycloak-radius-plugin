package com.github.vzakharchenko.radsec.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.apache.commons.codec.binary.Hex;
import org.jboss.logging.Logger;
import org.tinyradius.packet.PacketEncoder;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.server.RequestCtx;
import org.tinyradius.server.ResponseCtx;
import org.tinyradius.server.SecretProvider;
import org.tinyradius.util.RadiusEndpoint;
import org.tinyradius.util.RadiusPacketException;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.tinyradius.packet.RadiusPacket.HEADER_LENGTH;

@ChannelHandler.Sharable
public class RadSecCodec extends MessageToMessageCodec<ByteBuf, ResponseCtx> {

    private static final Logger LOGGER = Logger.getLogger(RadSecCodec.class);

    private final PacketEncoder packetEncoder;
    private final SecretProvider secretProvider;

    public RadSecCodec(
            PacketEncoder packetEncoder,
            SecretProvider secretProvider
    ) {
        super();
        this.packetEncoder = packetEncoder;
        this.secretProvider = secretProvider;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseCtx msg, List<Object> out) {
        try {
            RadiusPacket response = msg.getResponse();
            final RadiusPacket packet = response
                    .encodeResponse(msg.getEndpoint().getSecret(),
                            msg.getRequest().getAuthenticator());
            ByteBuf byteBuf = packetEncoder.toByteBuf(packet);
            out.add(byteBuf);
        } catch (RadiusPacketException e) {
            LOGGER.error("radius encode Error: " + e.getMessage(), e);
        }
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        decode(ctx, msg, out, true);
    }

    protected void decode(InetSocketAddress remoteAddress,
                          String sharedSecret,
                          RadiusPacket radiusPacket,
                          List<Object> out
    ) {
        if (out.stream().noneMatch(o -> Objects.equals(((RequestCtx) o)
                .getRequest().getIdentifier(), radiusPacket.getIdentifier()))) {
            out.add(new RequestCtx(radiusPacket,
                    new RadiusEndpoint(remoteAddress, sharedSecret)));
        }
    }

    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf msg,
                          List<Object> out,
                          boolean isNew) {
        try {
            final RadiusPacket radiusPacket = packetEncoder.fromByteBuf(msg);
            InetSocketAddress remoteAddress = (InetSocketAddress) ctx
                    .channel().remoteAddress();
            String sharedSecret = secretProvider.getSharedSecret(remoteAddress);
            radiusPacket.verify(sharedSecret, new byte[16]);
            decode(remoteAddress, sharedSecret, radiusPacket, out);

        } catch (RadiusPacketException e) {
            tryToFixPackage(ctx, msg, out, isNew);
        }
    }

    protected void tryToFixPackage(ChannelHandlerContext ctx, List<ByteBuf> byteBufs,
                                   List<Object> out) {
        for (ByteBuf byteBuf : byteBufs) {
            decode(ctx, byteBuf, out, false);
        }
    }

    protected void tryToFixPackage(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        try {
            List<ByteBuf> byteBufs = new ArrayList<>();
            fillPackages(msg, 0, byteBufs);
            tryToFixPackage(ctx, byteBufs, out);
        } catch (Exception ex) {
            LOGGER.error("radius decode Message " +
                    Hex.encodeHexString(getBytes(msg, msg.readableBytes())) +
                    " is Failed with " + ex.getMessage(), ex);
        }
    }

    protected void tryToFixPackage(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out,
                                   boolean isNew) {
        if (isNew) {
            tryToFixPackage(ctx, msg, out);
        } else {
            LOGGER.error("Not Valid Radius Packet " +
                    Hex.encodeHexString(getBytes(msg, msg.readableBytes())));
        }
    }

    protected void fillPackages(ByteBuffer content,
                                ByteBuf msg,
                                int pos,
                                List<ByteBuf> byteBufs) {
        int length = content.getShort(2);
        if (length > 0 && length <= content.remaining()) {
            byte[] bytes = getBytes(content, length);
            byteBufs.add(Unpooled.copiedBuffer(bytes));
            int newPos = pos + length;
            fillPackages(msg, newPos, byteBufs);
        } else {
            LOGGER.warn("Lost Packet " +
                    Hex.encodeHexString(getBytes(content, content.remaining())));
        }
    }

    protected void fillPackages(ByteBuf msg, int pos, List<ByteBuf> byteBufs) {
        ByteBuffer byteBuffer = msg.nioBuffer();
        ByteBuffer content = byteBuffer.position(pos);
        if (content.remaining() > HEADER_LENGTH) {
            fillPackages(content, msg, pos, byteBufs);
        }
    }

    private byte[] getBytes(ByteBuf msg, int length) {
        byte[] bytes = new byte[length];
        msg.nioBuffer().get(bytes);
        return bytes;
    }

    private byte[] getBytes(ByteBuffer content, int length) {
        byte[] bytes = new byte[length];
        content.get(bytes);
        return bytes;
    }


}
