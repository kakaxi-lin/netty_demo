package yk.socket.browserServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.InetSocketAddress;

public class BrowserServerThread implements Runnable{
	EventLoopGroup bossGroup=null;
	EventLoopGroup workerGroup=null;
    private int port;

    public BrowserServerThread(int port) {
        this.port = port;
    }

    public void run() {

    // netty服务端ServerBootstrap启动的时候,默认有两个eventloop分别是bossGroup和 workGroup 

        EventLoopGroup boosGroup = new NioEventLoopGroup();   // bossGroup
        EventLoopGroup workerGroup = new NioEventLoopGroup();  // workGroup
        try {
            ServerBootstrap sbs = new ServerBootstrap().group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                        	ch.pipeline().addLast("http-codec", new HttpServerCodec());
                        	ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
                        	ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                        	ch.pipeline().addLast("handler", new BrowserServerHandler());
                        };
                    }).option(ChannelOption.SO_BACKLOG, 1024)
            .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = sbs.bind(port).sync();
            System.out.println("Server start listen at " + port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void shutdown() {
		if (bossGroup != null)
			bossGroup.shutdownGracefully();
		if (workerGroup != null)
			workerGroup.shutdownGracefully();
	}
	
}
