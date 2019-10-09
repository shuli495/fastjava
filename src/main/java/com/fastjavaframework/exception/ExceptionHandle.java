package com.fastjavaframework.exception;

import com.alibaba.fastjson.JSON;
import com.fastjavaframework.FastjavaSpringBootConfig;
import com.fastjavaframework.common.ExceptionCodeTypeEnum;
import com.fastjavaframework.response.ReturnJson;
import com.fastjavaframework.util.VerifyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 异常处理类
 * 程序抛出的异常经过此类处理，含有ThrowPrompt.RETRUN_PROMPT信息的是抛出的提示，其他为异常
 * 异常信息不返回前台，只返回异常提示。
 *
 * @author wangshuli
 */
@ControllerAdvice
public class ExceptionHandle {
	private static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

	@Value("${spring.profiles.active}")
	private String env;


	@ExceptionHandler(Exception.class)
	@ResponseBody
	public Object javaException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
		this.privateErrorStack(null, ex.getMessage(), ex.getStackTrace(), Level.ERROR.toString());

		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return ex.getMessage();
	}

	/**
	 * 错误
	 * @param request
	 * @param response
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(ThrowPrompt.class)
	@ResponseBody
	public Object prompt(HttpServletRequest request, HttpServletResponse response, Exception ex) {
		ReturnJson returnJson = new ReturnJson(request);

		//错误信息
		ExceptionModel exceptionModel = null;
		if(VerifyUtils.isEmpty(ex.getMessage())) {
			// 格式化空指针异常
			exceptionModel = new ExceptionModel(HttpServletResponse.SC_INTERNAL_SERVER_ERROR+"",
					ExceptionCodeTypeEnum.NUMBER, "java.lang.NullPointerException", null);
		} else {
			exceptionModel = JSON.parseObject(ex.getMessage(), ExceptionModel.class);
		}

		String msg = exceptionModel.getMessage();
		String code = exceptionModel.getCode();
		String codeType = exceptionModel.getCodeType().toString();
		Object data = exceptionModel.getData();

		// 设置状态码
		int status = HttpServletResponse.SC_BAD_REQUEST;
		if(FastjavaSpringBootConfig.exception.responseOk()) {
			status = HttpServletResponse.SC_OK;
		} else if(ExceptionCodeTypeEnum.NUMBER.equals(codeType)) {
			status = Integer.valueOf(code);
		}
		response.setStatus(status);

		if(FastjavaSpringBootConfig.exception.promptStack()) {
			this.privateErrorStack(code, msg, ex.getStackTrace(), Level.WARN.toString());
		}

		return returnJson.prompt(msg, code, data);
	}

	/**
	 * 异常
	 * @param request
	 * @param response
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(ThrowException.class)
	@ResponseBody
	public Object exception(HttpServletRequest request, HttpServletResponse response, Exception ex) {
		ReturnJson returnJson = new ReturnJson(request);

		//错误信息
		ExceptionModel exceptionModel = null;
		if(VerifyUtils.isEmpty(ex.getMessage())) {
			// 格式化空指针异常
			exceptionModel = new ExceptionModel(HttpServletResponse.SC_INTERNAL_SERVER_ERROR+"",
					ExceptionCodeTypeEnum.NUMBER, "java.lang.NullPointerException", null);
		} else {
			try {
				exceptionModel = JSON.parseObject(ex.getMessage(), ExceptionModel.class);
			} catch (Exception e) {
				// 处理系统排出错误
				exceptionModel = new ExceptionModel(HttpServletResponse.SC_INTERNAL_SERVER_ERROR+"",
						ExceptionCodeTypeEnum.NUMBER, ex.getMessage(), null);
			}
		}

		String code = exceptionModel.getCode();
		String eMessage = exceptionModel.getMessage();
		String codeType = exceptionModel.getCodeType().toString();
		Object data = exceptionModel.getData();

		//异常日志
		this.privateErrorStack(code, eMessage, ex.getStackTrace(), Level.ERROR.toString());

		//返回提示信息
		String defMsg = FastjavaSpringBootConfig.exception.message();
		if(VerifyUtils.isNotEmpty(defMsg)) {
			eMessage = defMsg;
		}

		// 设置状态码
		int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		if(FastjavaSpringBootConfig.exception.responseOk()) {
			status = HttpServletResponse.SC_OK;
		} else if(ExceptionCodeTypeEnum.NUMBER.equals(codeType)) {
			status = Integer.valueOf(code);
		}
		response.setStatus(status);

		//返回前台异常
		return returnJson.exception(eMessage, code, data);
	}

	/**
	 * 打印异常日志
	 * @param code
	 * @param message
	 * @param stacks
	 */
	private void privateErrorStack(String code, String message, StackTraceElement[] stacks, String logLevel) {
		StringBuffer exMsg = new StringBuffer();

		if(VerifyUtils.isNotEmpty(code)) {
			exMsg.append("\nCode:").append(code);
		}

		exMsg.append("\n")
				.append("Exception:\n\t")
				.append(message).append("\n")
				.append("Stacks:\n");

		for(StackTraceElement stack : stacks) {
			exMsg.append("\t").append(stack).append("\n");
		}

		if(Level.WARN.toString().equals(logLevel)) {
			logger.warn(exMsg.toString());
		} else {
			logger.error(exMsg.toString());
		}
	}
}
