package com.fastjavaframework.base;

import com.fastjavaframework.response.ReturnJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wangshuli
 */
public class BaseController<B> {

	protected static Logger logger = LoggerFactory.getLogger(BaseController.class);

	@Autowired
	public HttpServletRequest request;
	
	@Autowired
	public HttpServletResponse response;
	
	@Autowired
	public B service;

	/**
	 * 返回success
	 */
	public Object success() {
		return new ReturnJson(request).success();
	}

	/**
	 * 返回Object json
	 * @param returnObj
	 * @return
	 */
	public Object success(Object returnObj) {
		return new ReturnJson(request).success(returnObj);
	}

	/**
	 * 返回key-value json
	 * @param returnStrKey
	 * @param returnStrVal
	 * @return
	 */
	public Object success(String returnStrKey, Object returnStrVal) {
		return new ReturnJson(request).success(returnStrKey, returnStrVal);
	}

	/**
	 * 返回提示信息
	 */
	public Object prompt(String message) {
		return new ReturnJson(request).prompt(message);
	}

	/**
	 * 返回异常
	 */
	public Object exception(String message) {
		return new ReturnJson(request).exception(message);
	}
}
