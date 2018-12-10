package yk.socket.test;

import com.alibaba.fastjson.JSON;

import yk.socket.po.UserApp;

public class TestJson {
	public static void main(String[] args) {
		String s="{'appId':'efs','userId':1}";
		UserApp u=JSON.parseObject(s, UserApp.class);
	}

}
