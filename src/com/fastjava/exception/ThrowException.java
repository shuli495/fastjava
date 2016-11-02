package com.fastjava.exception;

/**
 * 抛出异常
 */
public class ThrowException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public static final String RETRUN_EXCEPTION = "Exception:";	//返回前台提示 标记
	
	public ThrowException(String message) {
		super(RETRUN_EXCEPTION + message);
	}
}
