package com.fastjavaframework.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.fastjavaframework.response.ReturnJson;
import com.fastjavaframework.util.CommonUtil;
import com.fastjavaframework.util.VerifyUtils;

import java.util.Map;

/**
 * 异常处理类
 * 程序抛出的异常经过此类处理，含有ThrowPrompt.RETRUN_PROMPT信息的是抛出的提示，其他为异常
 * 异常信息不返回前台，只返回异常提示。
 */
public class ExceptionHandler implements HandlerExceptionResolver {
	private static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

	/**
	 * 自定义错误信息
	 */
	public String message = "";

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		ReturnJson returnJson = new ReturnJson(request);
		Object result = null;
		
		//错误信息 格式化空指针异常
		String nullPointer = HttpServletResponse.SC_INTERNAL_SERVER_ERROR + ThrowException.RETRUN_EXCEPTION + "java.lang.NullPointerException";
		String eMessage = VerifyUtils.isEmpty(ex.getMessage()) ? nullPointer : ex.getMessage();

		if(eMessage.indexOf(ThrowPrompt.RETRUN_PROMPT) != -1) {	//提示信息
			String[] prompts = eMessage.split(ThrowPrompt.RETRUN_PROMPT);
			String[] codes = prompts[0].split("@");

			String msg = prompts[1];
			String code = codes[0];
			String codeType = codes[1];

			if("int".equals(codeType)) {
				response.setStatus(Integer.valueOf(code));
				result = returnJson.prompt(msg);
			} else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				result = returnJson.prompt(msg, code);
			}
		} else {	//异常处理
			String[] exceptions = eMessage.split(ThrowException.RETRUN_EXCEPTION);

			String code = String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			if(exceptions.length == 1) {	//处理系统排出错误
				eMessage = exceptions[0];
			} else {
				code = exceptions[0];
				eMessage = exceptions[1];
			}

			StringBuffer exMsg = new StringBuffer();
			
			exMsg.append("\nCode:").append(code).append("\n")
				.append("Exception:\n\t")
				.append(eMessage).append("\n")
				.append("Method:\n");
			
			for(StackTraceElement stack : ex.getStackTrace()) {
				exMsg.append("\t").append(stack).append("\n");
			}
			
			logger.error(exMsg.toString());
			
			//返回提示信息
			if(VerifyUtils.isNotEmpty(message)) {
				eMessage = message;
			}

			if(code.indexOf("@") != -1) {
				String[] codes = code.split("@");
				String codeVal = codes[0];
				String codeType = codes[1];

				if("int".equals(codeType)) {
					response.setStatus(Integer.valueOf(codeVal));
					result = returnJson.exception(eMessage);
				} else {
					result = returnJson.exception(eMessage, codeVal);
				}
			} else {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result = returnJson.exception(eMessage);
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
