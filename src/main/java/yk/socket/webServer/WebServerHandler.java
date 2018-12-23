package yk.socket.webServer;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import yk.socket.config.NettyConfig;
import yk.socket.config.UserGroup;
import yk.socket.po.BaseMsg;

public class WebServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("客户端链接。。。");
		System.out.println(ctx.channel().id());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		System.out.println("客户端消息: " + msg);
		BaseMsg baseMsg = JSON.parseObject(msg.toString(), BaseMsg.class);
		//        ctx.writeAndFlush("哈哈笑");
		for (String userid : baseMsg.getUserList()) {
			System.out.println(userid);
			if (UserGroup.getUsers(baseMsg.getAppId(), userid) != null) {
				for (ChannelId id : NettyConfig.appUsers.get(baseMsg.getAppId()).get(userid).getChannelIdList()) {
					System.out.println(id);
					Channel c = NettyConfig.group.find(id);
					if (c != null && c.isActive()) {
						TextWebSocketFrame tws = new TextWebSocketFrame(baseMsg.getMessage());
						System.out.println("推送："+id);
						c.writeAndFlush(tws);
					}

				}

			}
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("客户端连接断开。。。");
	}

}
