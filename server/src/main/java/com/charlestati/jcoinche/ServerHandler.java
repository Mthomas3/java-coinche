package com.charlestati.jcoinche;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslHandler;

class ServerHandler extends SimpleChannelInboundHandler<String> {
    private final Server server;

    ServerHandler(Server server) {
        this.server = server;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(future -> server.newConnection(ctx));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        server.disconnected(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        server.newMessage(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        server.channelExceptionCaught(ctx);
    }
}
