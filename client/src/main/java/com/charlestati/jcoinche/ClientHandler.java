package com.charlestati.jcoinche;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

class ClientHandler extends SimpleChannelInboundHandler<String> {
    private Game game;

    ClientHandler(Game game) {
        this.game = game;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        this.game.handlerMsg(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
