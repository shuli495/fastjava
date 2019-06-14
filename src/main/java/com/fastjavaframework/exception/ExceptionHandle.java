package com.fastjavaframework.exception;

import com.alibaba.fastjson.JSON;
import com.fastjavaframework.FastjavaSpringBootConfig;
import com.fastjavaframework.common.ExceptionCodeTypeEnum;
import com.fastjavaframework.response.ReturnJson;
import com.fastjavaframework.util.VerifyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
		Object result = null;

		//错误信息
		ExceptionModel exceptionModel = null;
		if(VerifyUtils.isEmpty(ex.getMessage())) {
			// 格式化空指针异常
			exceptionModel = new ExceptionModel(HttpServletResponse.SC_INTERNAL_SERVER_ERROR+"",
					ExceptionCodeTypeEnum.NUMBER, "java.lang.NullPointerException");
		} else {
			exceptionModel = JSON.parseObject(ex.getMessage(), ExceptionModel.class);
		}

		String msg = exceptionModel.getMessage();
		String code = exceptionModel.getCode();
		String codeType = exceptionModel.getCodeType().toString();

		if(FastjavaSpringBootConfig.exception.isResponseStatus200()) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else if(ExceptionCodeTypeEnum.NUMBER.equals(codeType)) {
			response.setStatus(Integer.valueOf(code));
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

		result = returnJson.prompt(msg, code);

		//返回前台异常
		return result;
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
		Object result = null;

		//错误信息
		ExceptionModel exceptionModel = null;
		if(VerifyUtils.isEmpty(ex.getMessage())) {
			// 格式化空指针异常
			exceptionModel = new ExceptionModel(HttpServletResponse.SC_INTERNAL_SERVER_ERROR+"",
					ExceptionCodeTypeEnum.NUMBER, "java.lang.NullPointerException");
		} else {
			try {
				exceptionModel = JSON.parseObject(ex.getMessage(), ExceptionModel.class);
			} catch (Exception e) {
				// 处理系统排出错误
				exceptionModel = new ExceptionModel(HttpServletResponse.SC_INTERNAL_SERVER_ERROR+"",
						ExceptionCodeTypeEnum.NUMBER, ex.getMessage());
			}
		}

		String code = exceptionModel.getCode();
		String eMessage = exceptionModel.getMessage();
		String codeType = exceptionModel.getCodeType().toString();

		//返回提示信息
		String defMsg = FastjavaSpringBootConfig.exception.message();
		if(VerifyUtils.isNotEmpty(defMsg)) {
			eMessage = defMsg;
		}

		if(FastjavaSpringBootConfig.exception.isResponseStatus200()) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else if(ExceptionCodeTypeEnum.NUMBER.equals(codeType)) {
			response.setStatus(Integer.valueOf(code));
		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		result = returnJson.exception(eMessage, code);

		//异常日志
		StringBuffer exMsg = new StringBuffer();

		exMsg.append("\nCode:").append(code).append("\n")
				.append("Exception:\n\t")
				.append(eMessage).append("\n")
				.append("Method:\n");

		for(StackTraceElement stack : ex.getStackTrace()) {
			exMsg.append("\t").append(stack).append("\n");
		}

		logger.error(exMsg.toString());

		//返回前台异常
		return result;
	}
}
