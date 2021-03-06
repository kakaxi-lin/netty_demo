package yk.socket.browserServer;

import java.util.List;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import yk.socket.config.NettyConfig;
import yk.socket.config.UserGroup;
import yk.socket.po.UserApp;

public class BrowserServerHandler extends ChannelInboundHandlerAdapter {
	private WebSocketServerHandshaker handshaker;
	private static final String WEB_SOCKET_URL = "ws://localhost:8888/websocket";
	private UserApp tempUser;
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("客户端连接成功。。。");
		NettyConfig.group.add(ctx.channel());
		/*for(Channel a:NettyConfig.group){
			System.out.println(a.id());
		}*/
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("客户端连接断开。。。");
		NettyConfig.group.remove(ctx.channel());
		tempUser.setChannelId(ctx.channel().id());
		UserGroup.removeUsers(tempUser);
		/*System.out.println("断开连接。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。");
		System.out.println("channel个数。。"+NettyConfig.group.size());
		for(Channel a:NettyConfig.group){
			System.out.println(a.id());
		}
		System.out.println("-----------------------------");
		System.out.println("appid个数。。"+NettyConfig.appUsers.size());
		for( String key:NettyConfig.appUsers.keySet()){
			System.out.println("key:"+key);
			for(String key1:NettyConfig.appUsers.get(key).keySet()){
				System.out.println("key1:"+key1);
				UserApp xx = NettyConfig.appUsers.get(key).get(key1);
				List<ChannelId> cc = xx.getChannelIdList();
				for(ChannelId c:cc){
					System.out.println("ChannelId..."+c);
				}
			}
			
		}
		System.out.println("断开连接。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。");*/
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("Server channelRead....");
		//处理客户端向服务端发起http握手请求的业务
		if (msg instanceof FullHttpRequest) {
			handHttpRequest(ctx, (FullHttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) { //处理websocket连接业务
			handWebsocketFrame(ctx, (WebSocketFrame) msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("发生异常。。。。");
		cause.printStackTrace();
	}

	/**
	 * 处理客户端与服务端之前的websocket业务
	 * @param ctx
	 * @param frame
	 */
	private void handWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
		//判断是否是关闭websocket的指令
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
		}
		//判断是否是ping消息
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}

		//判断是否是二进制消息，如果是二进制消息，抛出异常
		if (!(frame instanceof TextWebSocketFrame)) {
			System.out.println("目前我们不支持二进制消息");
			return;
		}
		//返回应答消息
		//获取客户端向服务端发送的消息
		String request = ((TextWebSocketFrame) frame).text();
		System.out.println("服务端收到客户端的消息====>>>" + request);
		UserApp user=JSON.parseObject(request, UserApp.class);
		System.out.println(user.getAppId());
		System.out.println(user.getUserId());
		TextWebSocketFrame tws = new TextWebSocketFrame("channel.id:" + ctx.channel().id() + " ===>>> " + request);
//		ctx.writeAndFlush(tws);
		System.out.println("当前channel_id:"+ctx.channel().id());
		//群发，服务端向每个连接上来的客户端群发消息
//		NettyConfig.group.writeAndFlush(new TextWebSocketFrame("vvvv"));
		
		UserApp u = UserGroup.getUsers(user.getAppId(), user.getUserId());
		if (u == null) {
			u=new UserApp();
			u.setUserId(user.getUserId());
			u.setAppId(user.getAppId());
			u.setChannelIdList(ctx.channel().id());
			UserGroup.addUsers(u);
		} else {
			
			u.setChannelIdList(ctx.channel().id());

		}
		tempUser=u;
		System.out.println("channel个数。。"+NettyConfig.group.size());
		for(Channel a:NettyConfig.group){
			System.out.println(a.id());
		}
		System.out.println("appid个数。。"+NettyConfig.appUsers.size());
		for( String key:NettyConfig.appUsers.keySet()){
			System.out.println("key:"+key);
			for(String key1:NettyConfig.appUsers.get(key).keySet()){
				System.out.println("key1:"+key1);
				UserApp xx = NettyConfig.appUsers.get(key).get(key1);
				List<ChannelId> cc = xx.getChannelIdList();
				for(ChannelId c:cc){
					System.out.println("ChannelId..."+c);
				}
			}
			/*for(ChannelId x:s.getChannelIds()){
				System.out.println(x);
			}*/
		}
//		NettyConfig.group.writeAndFlush(tws);
	}

	/**
	 * 处理客户端向服务端发起http握手请求的业务
	 * @param ctx
	 * @param req
	 */
	private void handHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
		if (!req.decoderResult().isSuccess() || !("websocket".equals(req.headers().get("Upgrade")))) {
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(WEB_SOCKET_URL, null, false);
		handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			handshaker.handshake(ctx.channel(), req);
		}
	}

	/**
	 * 服务端向客户端响应消息
	 * @param ctx
	 * @param req
	 * @param res
	 */
	private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
		System.out.println(res.status().code());
		if (res.status().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
		}
		//服务端向客户端发送数据
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (res.status().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}
}
