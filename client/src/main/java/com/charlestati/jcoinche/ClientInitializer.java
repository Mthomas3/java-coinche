package com.charlestati.jcoinche;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;

class ClientInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;
    private final String host;
    private final int port;
    private Game game;

    ClientInitializer(SslContext sslCtx, String host, int port, Game game) {
        this.sslCtx = sslCtx;
        this.host = host;
        this.port = port;
        this.game = game;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(sslCtx.newHandler(ch.alloc(), host, port));

        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());

        pipeline.addLast(new ClientHandler(game));

    }
}
