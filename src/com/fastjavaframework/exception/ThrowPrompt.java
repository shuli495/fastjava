package com.fastjavaframework.exception;

import javax.servlet.http.HttpServletResponse;

/**
 * 返回前台 提示信息
 */
public class ThrowPrompt extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public static final String RETRUN_PROMPT = "@Prompt:";	//返回前台提示 标记
	
    public ThrowPrompt(String msg) {
		super(HttpServletResponse.SC_BAD_REQUEST + RETRUN_PROMPT + msg);
    }

	public ThrowPrompt(String msg, String code) {
		super(code + RETRUN_PROMPT + msg);
	}
}
