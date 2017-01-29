package com.fastjava.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.fastjava.response.ReturnJson;
import com.fastjava.util.CommonUtil;
import com.fastjava.util.VerifyUtils;

/**
 * 异常处理类
 * 程序抛出的异常经过此类处理，含有ThrowPrompt.RETRUN_PROMPT信息的是抛出的提示，其他为异常
 * 异常信息不返回前台，只返回异常提示。
 */
public class ExceptionHandler implements HandlerExceptionResolver {
	private static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);
	
	//自定义错误信息
	public String message = "";

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		ReturnJson returnJson = new ReturnJson(request);
		Object result = null;
		
		//错误信息 格式化空指针异常
		String eMessage = VerifyUtils.isEmpty(ex.getMessage())?"java.lang.NullPointerException":ex.getMessage();
		
		if(eMessage.startsWith(ThrowPrompt.RETRUN_PROMPT)) {	//提示信息
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	//状态码为400
			result = returnJson.prompt(ex.getMessage());
		} else {	//异常处理
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);	//状态码为500
			StringBuffer exMsg = new StringBuffer();
			
			exMsg.append("\nException:\n\t")
				.append(eMessage.replace(ThrowException.RETRUN_EXCEPTION, "")).append("\n")
				.append("Method:\n");
			
			for(StackTraceElement stack : ex.getStackTrace()) {
				exMsg.append("\t").append(stack).append("\n");
			}
			
			logger.error(exMsg.toString());
			
			//返回提示信息
			if("".equals(message)) {
				result = returnJson.exception(eMessage);
			} else {
				result = returnJson.exception(message);
			}
		}

		//返回前台异常
		CommonUtil.setResponseReturnValue(response, response.getStatus(), JSON.toJSON(result).toString());
		return new ModelAndView();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
