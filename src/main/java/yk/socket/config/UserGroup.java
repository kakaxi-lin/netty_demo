package yk.socket.config;

import java.util.concurrent.ConcurrentMap;


import io.netty.util.internal.PlatformDependent;
import yk.socket.po.UserApp;

public class UserGroup {
	public static void addUsers(UserApp u) {
		//efs<userID,User>
		ConcurrentMap<String, UserApp> cm = NettyConfig.appUsers.get(u.getAppId());
		if (cm == null) {
			cm = PlatformDependent.newConcurrentHashMap();
		}
		cm.put(u.getUserId(), u);
		NettyConfig.appUsers.put(u.getAppId(), cm);
//		
		
	}

	public static UserApp getUsers(String appId, String userId) {
		ConcurrentMap<String, UserApp> cm = NettyConfig.appUsers.get(appId);
		if (cm == null) {
			return null;
		} else {
			return cm.get(userId);
		}
	}
	
	public static void removeUsers(UserApp u) {
		System.out.println(u.getAppId());
		System.out.println(u.getUserId());
		System.out.println(u.getChannelId());
		ConcurrentMap<String, UserApp> cm = NettyConfig.appUsers.get(u.getAppId());
		if (cm == null) {
			return;
		}
		UserApp user=cm.get(u.getUserId());
		user.getChannelIdList().remove(u.getChannelId());
		if(user.getChannelIdList().size()==0){
			cm.remove(u.getUserId());
			NettyConfig.appUsers.remove(u.getAppId());
		}
	}

}
