package yk.socket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端链接。。。");
        System.out.println(ctx.channel().id());
}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("客户端消息: "+msg);
        ctx.writeAndFlush("哈哈笑");
    }

    @Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("客户端连接断开。。。");
	}
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
