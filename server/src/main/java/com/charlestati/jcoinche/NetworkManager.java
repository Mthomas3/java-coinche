package com.charlestati.jcoinche;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

class NetworkManager {
    private static final int EXIT_FAILURE = 84;
    private final ServerBootstrap bootstrap;
    private final NioEventLoopGroup bossGroup;
    private final NioEventLoopGroup workerGroup;

    NetworkManager(Server server) {
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerInitializer(server, sslCtx));
        } catch (CertificateException | SSLException e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.exit(EXIT_FAILURE);
        }
    }

    void startListening(int port) throws InterruptedException {
        try {
            bootstrap.bind(port).sync().channel().closeFuture().sync();
            System.out.format("Listening on port %d%n", port);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
