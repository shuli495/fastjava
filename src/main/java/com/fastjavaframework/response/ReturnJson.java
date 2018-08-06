package com.fastjavaframework.response;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.exception.ThrowPrompt;
import com.fastjavaframework.util.VerifyUtils;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * 返回前台json格式数据
 *
 * @author wangshuli
 */
public class ReturnJson {

	/**
	 * jsonp模式callback关键词
	 */
	private String jsonpCallback = "";

	public ReturnJson() {
	}

	/**
	 * 从request中自动获取名为jsonpCallback的参数，判断是否返回jsonp
	 * @param request
     */
	public ReturnJson(HttpServletRequest request) {
		if(null == request || !HttpMethod.GET.name().equalsIgnoreCase(request.getMethod())) {
			return;
		}

		Enumeration names = request.getParameterNames();

		String callback = "";
		while(names.hasMoreElements()) {
			String name = names.nextElement().toString();
			if("callback".equalsIgnoreCase(name)) {
				String value = request.getParameter(name);
				callback = "".equals(value)?"callback":value;
				break;
			}
		}
		if(!"".equals(callback)) {
			this.jsonpCallback = callback;
		}
	}

	public ReturnJson(String jsonpCallback) {
		this.jsonpCallback = jsonpCallback;
	}

	/**
	 * 处理返回信息
	 * @param type 返回信息类型 success成功 prompt提示 excepiton异常
	 * @param code 返回状态码
	 * @param reObj 返回信息
	 * @return Response
	 */
	private Object setReturnInfo(String type, String code, Object reObj, String returnKey) {
		Object data = null;
		String message = "success";

		if(null != reObj) {
			//返回键值对
			if(VerifyUtils.isNotEmpty(returnKey)) {
				Map<String,Object> reStrByKeyMap = new HashMap<>(1);
				reStrByKeyMap.put(returnKey.toString(), reObj);
				data = reStrByKeyMap;
			} else {
				//返回对象
				data = reObj;
			}
		}

		//提示
		if(ThrowPrompt.RETRUN_PROMPT_NAME.equals(type)) {
			message = reObj.toString();
			data = null;
		} else if(ThrowException.RETRUN_EXCEPTION_NAME.equals(type)) {
			//异常
			if(!VerifyUtils.isEmpty(reObj)) {
				message = reObj.toString();
			} else {
				message = "请稍后再试！";
			}
			data = null;
		}

		Result result = new Result();
		result.setStatus(type);
		result.setMessage(message);
		result.setData(data == null ? "" : data);

		if(VerifyUtils.isNotEmpty(code)) {
			result.setCode(code);
		}

		if(VerifyUtils.isEmpty(this.jsonpCallback)) {
			// json模式
			return result;
		} else {
			// jsonp模式
			return this.jsonpCallback + "(" + JSONObject.toJSONString(result) + ")";
		}
	}
	
	/**
	 * 返回success
	 */
	public Object success() {
		return this.setReturnInfo("success", null, "success", null);
	}
	
	/**
	 * 返回Object json
	 * @param returnObj
	 * @return
	 */
	public Object success(Object returnObj) {
		return this.setReturnInfo("success", null, returnObj, null);
	}

	/**
	 * 返回Object json
	 * @param returnObj
	 * @param code
	 * @return
	 */
	public Object success(Object returnObj, String code) {
		return this.setReturnInfo("success", code, returnObj, null);
	}

	/**
	 * 返回key-value json
	 * @param returnStrKey
	 * @param returnStrVal
	 * @return
	 */
	public Object success(String returnStrKey, Object returnStrVal) {
		return setReturnInfo("success", null, returnStrVal, returnStrKey);
	}

	/**
	 * 返回key-value json
	 * @param returnStrKey
	 * @param returnStrVal
	 * @param code
	 * @return
	 */
	public Object success(String returnStrKey, Object returnStrVal, String code) {
		return setReturnInfo("success", code, returnStrVal, returnStrKey);
	}

	/**
	 * 返回提示信息
	 */
	public Object prompt(String message) {
		return setReturnInfo("prompt", null, message, null);
	}

	/**
	 * 返回提示信息
	 */
	public Object prompt(String message, String code) {
		return setReturnInfo("prompt", code, message, null);
	}

	/**
	 * 返回异常
	 */
	public Object exception(String message) {
		return setReturnInfo("exception", null, message, null);
	}

	/**
	 * 返回异常
	 */
	public Object exception(String message, String code) {
		return setReturnInfo("exception", code, message, null);
	}
	
}
