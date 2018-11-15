package yk.socket;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.bootstrap.Bootstrap;

public class Client {

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8888"));
    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("http-decoder", new HttpResponseDecoder());
							ch.pipeline().addLast("http-encoder", new HttpRequestEncoder());
							ch.pipeline().addLast("handler", new ClientHandler());
                        }
                    });

            ChannelFuture future = b.connect(HOST, PORT).sync();
            future.channel().writeAndFlush("Hello Netty Server, I am a common client");
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
