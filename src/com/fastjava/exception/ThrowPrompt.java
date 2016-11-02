package com.fastjava.exception;

/**
 * 返回前台 提示信息
 */
public class ThrowPrompt extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public static final String RETRUN_PROMPT = "Prompt:";	//返回前台提示 标记
	
    public ThrowPrompt(String msg) {
		super(RETRUN_PROMPT + msg);
    }
}
