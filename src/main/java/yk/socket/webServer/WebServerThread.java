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

public class WebServerThread implements Runnable{
	EventLoopGroup bossGroup=null;
	EventLoopGroup workerGroup=null;
	private int port;

	public WebServerThread(int port) {
		this.port = port;
	}

	public void run() {

		// netty服务端ServerBootstrap启动的时候,默认有两个eventloop分别是bossGroup和 workGroup 

		EventLoopGroup bossGroup = new NioEventLoopGroup(); // bossGroup
		EventLoopGroup workerGroup = new NioEventLoopGroup(); // workGroup
		try {
			ServerBootstrap sbs = new ServerBootstrap().group(bossGroup, workerGroup)
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
			future.channel().closeFuture().sync();
			
			
		} catch (Exception e) {
			bossGroup.shutdownGracefully();
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
