package yk.socket.config;

import java.util.concurrent.ConcurrentMap;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.PlatformDependent;
import yk.socket.po.UserApp;

/**
 * 存储整个工程的全局配置
 * @author liuyazhuang
 *
 */
public class NettyConfig {
	
	/**
	 * 存储每一个客户端接入进来时的channel对象
	 */
	public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	/**
	 * appid-userid-AppUser,映射关系如果过多可以考虑使用缓存技术，比如redis等等
	 */
	public final static ConcurrentMap<String,ConcurrentMap<String, UserApp>> appUsers = PlatformDependent.newConcurrentHashMap();
}
