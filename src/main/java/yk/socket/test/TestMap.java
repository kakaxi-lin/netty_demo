package yk.socket.test;

import java.util.HashMap;
import java.util.Map;

import yk.socket.po.UserApp;

public class TestMap {
	public static void main(String[] args) {
		Map<String,UserApp> map=new HashMap<String,UserApp>();
		UserApp u=new UserApp();
		u.setAppId("aaa");
		u.setUserId("111");
		map.put("aa", u);
		UserApp u1=map.get("aa");
		u1.setAppId("aaa1");
		u1.setUserId("1112");
		System.out.println(map.get("aa").getAppId());
		System.out.println(map.get("aa").getUserId());
	}

}
