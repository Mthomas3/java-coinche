package com.charlestati.jcoinche;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client {
    private static final int EXIT_FAILURE = 84;
    private static final int EXIT_SUCCESS = 0;
    private final String host;
    private final int port;
    private final Bootstrap bootstrap;
    private final NioEventLoopGroup group;
    private Channel ch;
    private ChannelFuture lastSentenceSend;
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));


    Client(String host, int port, Game game) {
        this.host = host;
        this.port = port;

        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();

        try {
            final SslContext sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new ClientInitializer(sslCtx, host, port, game));

        } catch (SSLException e) {
            e.printStackTrace();
            group.shutdownGracefully();
            System.exit(EXIT_FAILURE);
        }
        try {
            this.initChannel();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(EXIT_SUCCESS);
        }
    }

    void SyncWritting() throws InterruptedException {
        try {
            if (this.lastSentenceSend != null)
                this.lastSentenceSend.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(EXIT_FAILURE);
        }
    }

    String getMessageFromPlayer() throws Exception {
        return this.in.readLine();
    }

    void sendMessageToserver(String message) {
        this.lastSentenceSend = this.ch.writeAndFlush(message + "\r\n");
    }

    private void initChannel() throws InterruptedException {
        this.ch = bootstrap.connect(host, port).sync().channel();
    }

    NioEventLoopGroup getGroup() {
        return this.group;
    }

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : System.getProperty("host", "127.0.0.1");
        String port = args.length > 1 ? args[1] : System.getProperty("port", "8080");

            try {
                Game game = new Game(host, port);
                game.run();
            } catch (Exception e) {
                System.out.println("Error occured: ");
                e.printStackTrace();
                System.exit(EXIT_FAILURE);
            }


    }
}
