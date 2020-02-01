package com.github.vzakharchenko.radius.radius.handlers;

import io.netty.channel.ChannelHandlerContext;
import org.tinyradius.server.RequestCtx;
import org.tinyradius.server.handler.RequestHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractThreadRequestHandler
        extends RequestHandler {

    private static final ExecutorService EXECUTOR_SERVICE = Executors
            .newCachedThreadPool();

    @Override
    protected final void channelRead0(ChannelHandlerContext ctx, RequestCtx msg) {
        EXECUTOR_SERVICE.execute(() -> channelReadRadius(ctx, msg));
    }

    protected abstract void channelReadRadius(ChannelHandlerContext ctx, RequestCtx msg);

}
