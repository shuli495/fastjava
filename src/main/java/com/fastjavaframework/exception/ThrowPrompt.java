package com.fastjavaframework.exception;

import com.alibaba.fastjson.JSON;
import com.fastjavaframework.common.ExceptionCodeTypeEnum;

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
	public static final String RETRUN_PROMPT_NAME = "prompt";

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
		super(JSON.toJSONString(new ExceptionModel(HttpServletResponse.SC_BAD_REQUEST + "",
				ExceptionCodeTypeEnum.NUMBER, message, null)));

		this.message = message;
	}

	public ThrowPrompt(String message, Object data) {
		super(JSON.toJSONString(new ExceptionModel(HttpServletResponse.SC_BAD_REQUEST + "",
				ExceptionCodeTypeEnum.NUMBER, message, data)));

		this.message = message;
	}

	/**
	 * HTTP状态码为400，返回json数据中包含code
	 * @param message
	 * @param code
     */
	public ThrowPrompt(String message, String code) {
		super(JSON.toJSONString(new ExceptionModel(code, ExceptionCodeTypeEnum.STRING, message, null)));

		this.message = message;
		this.code = code;
	}

	/**
	 * HTTP状态码为400，返回json数据中包含code
	 * @param message
	 * @param code
	 */
	public ThrowPrompt(String message, String code, Object data) {
		super(JSON.toJSONString(new ExceptionModel(code, ExceptionCodeTypeEnum.STRING, message, data)));

		this.message = message;
		this.code = code;
	}

	/**
	 * HTTP状态码为code
	 * @param message
	 * @param code
     */
	public ThrowPrompt(String message, int code) {
		super(JSON.toJSONString(new ExceptionModel(code + "", ExceptionCodeTypeEnum.NUMBER, message, null)));

		this.message = message;
		this.code = String.valueOf(code);
	}

	/**
	 * HTTP状态码为code
	 * @param message
	 * @param code
	 */
	public ThrowPrompt(String message, int code, Object data) {
		super(JSON.toJSONString(new ExceptionModel(code + "", ExceptionCodeTypeEnum.NUMBER, message, data)));

		this.message = message;
		this.code = String.valueOf(code);
	}
}
