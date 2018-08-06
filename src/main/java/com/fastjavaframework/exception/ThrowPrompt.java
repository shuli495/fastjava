package com.fastjavaframework.exception;

import javax.servlet.http.HttpServletResponse;

/**
 * 返回前台 提示信息
 *
 * @author wangshuli
 */
public class ThrowPrompt extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * 返回前台提示 标记
	 */
	public static final String RETRUN_PROMPT = "@Prompt:";
	public static final String RETRUN_PROMPT_NAME = "prompt:";

	/**
	 * http状态码
	 */
	private String code;

	/**
	 * 错误信息
	 */
	private String message;

	public String getCode() {
		return this.code;
	}

	public String getDetailMessage() {
		return this.message;
	}
	
    public ThrowPrompt(String message) {
		super(HttpServletResponse.SC_BAD_REQUEST + "@int" + RETRUN_PROMPT + message);

		this.message = message;
    }

	/**
	 * HTTP状态码为400，返回json数据中包含code
	 * @param message
	 * @param code
     */
	public ThrowPrompt(String message, String code) {
		super(code + "@String" + RETRUN_PROMPT + message);

		this.message = message;
		this.code = code;
	}

	/**
	 * HTTP状态码为code
	 * @param message
	 * @param code
     */
	public ThrowPrompt(String message, int code) {
		super(code + "@int" + RETRUN_PROMPT + message);

		this.message = message;
		this.code = String.valueOf(code);
	}
}
