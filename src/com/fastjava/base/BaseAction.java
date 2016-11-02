package com.fastjava.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.fastjava.response.ReturnJson;

public class BaseAction<B extends BaseService<?, ?>> extends ReturnJson {

	@Autowired
	public HttpServletRequest request;
	
	@Autowired
	public HttpServletResponse response;
	
	@Autowired
	public B service;
	
	
	/**
	 * 判断用户是否已登录
	 * @return
	 */
	public String loginState(){
		String ssloginuserid =  (String)request.getSession().getAttribute("ssloginuserid");
		if(null == ssloginuserid){
			return null;
		}
		return ssloginuserid;
	}
}
