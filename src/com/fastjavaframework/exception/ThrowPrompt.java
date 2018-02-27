package com.fastjavaframework.exception;

import javax.servlet.http.HttpServletResponse;

/**
 * 返回前台 提示信息
 */
public class ThrowPrompt extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public static final String RETRUN_PROMPT = "@Prompt:";	//返回前台提示 标记
	
    public ThrowPrompt(String msg) {
		super(HttpServletResponse.SC_BAD_REQUEST + "@int" + RETRUN_PROMPT + msg);
    }

	/**
	 * HTTP状态码为400，返回json数据中包含code
	 * @param message
	 * @param code
     */
	public ThrowPrompt(String message, String code) {
		super(code + "@String" + RETRUN_PROMPT + message);
	}

	/**
	 * HTTP状态码为code
	 * @param message
	 * @param code
     */
	public ThrowPrompt(String message, int code) {
		super(code + "@int" + RETRUN_PROMPT + message);
	}
}
