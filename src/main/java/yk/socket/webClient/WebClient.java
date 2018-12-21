package yk.socket.webClient;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.alibaba.fastjson.JSON;

import io.netty.bootstrap.Bootstrap;

public class WebClient {

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "7777"));
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
							ch.pipeline().addLast("decoder", new StringDecoder());
							ch.pipeline().addLast("encoder", new StringEncoder());
							ch.pipeline().addLast("handler", new WebClientHandler());
                        }
                    });

            ChannelFuture future = b.connect(HOST, PORT).sync();
            BaseMsg msg=new BaseMsg();
            msg.setAppId("efs");
            List<String> list = new ArrayList<String>();
            list.add("1");
            msg.setMessage("已经报警了"+new Random().nextInt(9));
			msg.setUserList(list);
            String xx=JSON.toJSONString(msg);
            System.out.println("xx:"+xx);
			future.channel().writeAndFlush(xx);
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
