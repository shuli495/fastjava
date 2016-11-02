package com.fastjava;

import java.util.HashMap;
import java.util.Map;

public class UserSession {
	
	private String userId;
	private Map<String,String> menuAuthority = new HashMap<>();
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public Map<String, String> getMenuAuthority() {
		return menuAuthority;
	}
	
	public void setMenuAuthority(Map<String, String> menuAuthority) {
		this.menuAuthority = menuAuthority;
	}
	
	
}
