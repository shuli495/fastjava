package com.fastjava.exception;

import javax.servlet.http.HttpServletResponse;

/**
 * 抛出异常
 */
public class ThrowException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public static final String RETRUN_EXCEPTION = "@Exception:";	//返回前台提示 标记

	public ThrowException(String message) {
		super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR + RETRUN_EXCEPTION + message);
	}

	public ThrowException(String message, int code) {
		super(code + RETRUN_EXCEPTION + message);
	}
}
