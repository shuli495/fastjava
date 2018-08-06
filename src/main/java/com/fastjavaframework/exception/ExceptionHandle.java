package com.fastjavaframework.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastjavaframework.FastjavaSpringBootConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fastjavaframework.response.ReturnJson;
import com.fastjavaframework.util.VerifyUtils;


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

		//错误信息 格式化空指针异常
		String nullPointer = HttpServletResponse.SC_INTERNAL_SERVER_ERROR + ThrowException.RETRUN_EXCEPTION + "java.lang.NullPointerException";
		String eMessage = VerifyUtils.isEmpty(ex.getMessage()) ? nullPointer : ex.getMessage();

		String[] prompts = eMessage.split(ThrowPrompt.RETRUN_PROMPT);
		String[] codes = prompts[0].split("@");

		String msg = prompts[1];
		String code = codes[0];
		String codeType = codes[1];

		if(int.class.getName().equals(codeType)) {
			response.setStatus(Integer.valueOf(code));
			result = returnJson.prompt(msg, code);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result = returnJson.prompt(msg, code);
		}

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

		//错误信息 格式化空指针异常
		String nullPointer = HttpServletResponse.SC_INTERNAL_SERVER_ERROR + ThrowException.RETRUN_EXCEPTION + "java.lang.NullPointerException";
		String eMessage = VerifyUtils.isEmpty(ex.getMessage()) ? nullPointer : ex.getMessage();

		String[] exceptions = eMessage.split(ThrowException.RETRUN_EXCEPTION);

		String code = String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//处理系统排出错误
		if(exceptions.length == 1) {
			eMessage = exceptions[0];
		} else {
			code = exceptions[0];
			eMessage = exceptions[1];
		}

		//返回提示信息
		String defMsg = FastjavaSpringBootConfig.Exception.message;
		if(VerifyUtils.isNotEmpty(defMsg)) {
			eMessage = defMsg;
		}

		String codeStr = String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		if(code.indexOf("@") != -1) {
			String[] codes = code.split("@");
			String codeVal = codes[0];
			String codeType = codes[1];

			if(int.class.getName().equals(codeType)) {
				response.setStatus(Integer.valueOf(codeVal));
				result = returnJson.exception(eMessage, codeVal);
			} else {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result = returnJson.exception(eMessage, codeVal);
			}

			codeStr = codeVal;
		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			result = returnJson.exception(eMessage, code);
		}

		//异常日志
		StringBuffer exMsg = new StringBuffer();

		exMsg.append("\nCode:").append(codeStr).append("\n")
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
