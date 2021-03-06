package yk.socket.webServer;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import yk.socket.config.NettyConfig;

public class WebServer {
	private int port;

	public WebServer(int port) {
		this.port = port;
	}

	public void start() {

		// netty服务端ServerBootstrap启动的时候,默认有两个eventloop分别是bossGroup和 workGroup 

		EventLoopGroup boosGroup = new NioEventLoopGroup(); // bossGroup
		EventLoopGroup workerGroup = new NioEventLoopGroup(); // workGroup
		try {
			ServerBootstrap sbs = new ServerBootstrap().group(boosGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.localAddress(new InetSocketAddress(port))
					.childHandler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast("decoder", new StringDecoder());
					ch.pipeline().addLast("encoder", new StringEncoder());
					ch.pipeline().addLast(new WebServerHandler());
				};
			}).option(ChannelOption.SO_BACKLOG, 1024)
			.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture future = sbs.bind(port).sync();
			System.out.println("Server start listen at " + port);
			System.out.println("user...size..."+NettyConfig.appUsers.size());
			System.out.println("channel...size..."+NettyConfig.group.size());
			future.channel().closeFuture().sync();
			
			
		} catch (Exception e) {
			boosGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 7777;
		}
		new WebServer(port).start();
	}

}
