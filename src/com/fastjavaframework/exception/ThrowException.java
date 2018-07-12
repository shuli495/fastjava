package com.fastjavaframework.exception;

import javax.servlet.http.HttpServletResponse;

/**
 * 抛出异常
 */
public class ThrowException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public static final String RETRUN_EXCEPTION = "@Exception:";	//返回前台提示 标记

	private String code;
	private String message;

	public String getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	public ThrowException(String message) {
		super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR + "@int" + RETRUN_EXCEPTION + message);

		this.message = message;
	}

	/**
	 * HTTP状态码为400，返回json数据中包含code
	 * @param message
	 * @param code
	 */
	public ThrowException(String message, String code) {
		super(code + "@String" + RETRUN_EXCEPTION + message);

		this.message = message;
		this.code = code;
	}

	/**
	 * HTTP状态码为code
	 * @param message
	 * @param code
	 */
	public ThrowException(String message, int code) {
		super(code + "@int" + RETRUN_EXCEPTION + message);

		this.message = message;
		this.code = String.valueOf(code);
	}
}
