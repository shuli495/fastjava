package com.fastjavaframework.exception;

import com.alibaba.fastjson.JSON;
import com.fastjavaframework.common.ExceptionCodeTypeEnum;

import javax.servlet.http.HttpServletResponse;

/**
 * 抛出异常
 *
 * @author wangshuli
 */
public class ThrowException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * 返回前台提示 标记
	 */
	public static final String RETRUN_EXCEPTION_NAME = "exception";

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

	public ThrowException(String message) {
		super(JSON.toJSONString(new ExceptionModel(HttpServletResponse.SC_INTERNAL_SERVER_ERROR + "",
				ExceptionCodeTypeEnum.NUMBER, message)));

		this.message = message;
	}

	/**
	 * HTTP状态码为400，返回json数据中包含code
	 * @param message
	 * @param code
	 */
	public ThrowException(String message, String code) {
		super(JSON.toJSONString(new ExceptionModel(code, ExceptionCodeTypeEnum.STRING, message)));

		this.message = message;
		this.code = code;
	}

	/**
	 * HTTP状态码为code
	 * @param message
	 * @param code
	 */
	public ThrowException(String message, int code) {
		super(JSON.toJSONString(new ExceptionModel(code + "", ExceptionCodeTypeEnum.NUMBER, message)));

		this.message = message;
		this.code = String.valueOf(code);
	}
}
