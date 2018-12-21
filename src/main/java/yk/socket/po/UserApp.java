package yk.socket.po;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelId;

public class UserApp {
	public String userId;
	public String appId;
	public ChannelId channelId;
	private final List<ChannelId> channelIdList = new ArrayList<>();//用户所持有的活跃通道

	public String getUserId() {
		return userId;
	}

	public void setChannelIdList(ChannelId channelId) {
		if(!channelIdList.contains(channelId)){
			this.channelIdList.add(channelId);
		}
	}

	public List<ChannelId> getChannelIdList() {
		return channelIdList;
	}

	public ChannelId getChannelId() {
		return channelId;
	}

	public void setChannelId(ChannelId channelId) {
		this.channelId = channelId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

}
