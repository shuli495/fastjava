package com.fastjavaframework.response;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.fastjavaframework.util.VerifyUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 返回前台json格式数据
 */
public class ReturnJson {

	private String jsonpCallback = "";	//jsonp模式callback关键词

	public ReturnJson() {
	}

	/**
	 * 从request中自动获取名为jsonpCallback的参数，判断是否返回jsonp
	 * @param request
     */
	public ReturnJson(HttpServletRequest request) {
		if(!"get".equalsIgnoreCase(request.getMethod())) {
			return;
		}

		Enumeration names = request.getParameterNames();

		String callback = "";
		while(names.hasMoreElements()) {
			String name = names.nextElement().toString();
			if(name.equalsIgnoreCase("callback")) {
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
	 * @param reObj 返回信息
	 * @return Response
	 */
	private Result setReturnInfo(String type, Object reObj, String returnKey) {
		Object data = null;
		
		if("success".equals(type)) {	//成功
			if(!VerifyUtils.isEmpty(returnKey)) {	//返回键值对
				Map<String,Object> reStrByKeyMap = new HashMap<>();
				reStrByKeyMap.put(returnKey.toString(), reObj);
				data = reStrByKeyMap;
			} else {	//返回对象
				data = reObj;
			}
		} else if("prompt".equals(type)) {	//提示
			data = reObj.toString();
		} else if("exception".equals(type)) {	//异常
			if(!VerifyUtils.isEmpty(reObj)) {
				data = reObj.toString();
			} else {
				data = "请稍后再试！";
			}
		}

		Result result = new Result();
		result.setStatus(type);
		result.setData(data == null ? "" : data);
		
		return result;
	}
	
	/**
	 * 返回success
	 */
	public Object success() {
		Result result = setReturnInfo("success", "success", null);

		if(VerifyUtils.isEmpty(this.jsonpCallback)) {
			return result;
		} else {
			return this.jsonpCallback + "(" + JSONObject.toJSONString(result) + ")";
		}
	}
	
	/**
	 * 返回Object json
	 * @param returnObj
	 * @return
	 */
	public Object success(Object returnObj) {
		Result result = setReturnInfo("success", returnObj, null);

		if(VerifyUtils.isEmpty(this.jsonpCallback)) {
			// json模式
			return result;
		} else {
			// jsonp模式
			return this.jsonpCallback + "(" + JSONObject.toJSONString(result) + ")";
		}
	}
	
	/**
	 * 返回string json
	 * @param returnStr
	 * @return
	 */
	public Object success(String returnStr) {
		Result result = setReturnInfo("success", returnStr, null);

		if(VerifyUtils.isEmpty(this.jsonpCallback)) {
			// json模式
			return result;
		} else {
			// jsonp模式
			return this.jsonpCallback + "(" + JSONObject.toJSONString(result) + ")";
		}
	}
	
	/**
	 * 返回key-value json
	 * @param returnStrKey
	 * @param returnStrVal
	 * @return
	 */
	public Object success(String returnStrKey, Object returnStrVal) {
		Result result = setReturnInfo("success", returnStrVal, returnStrKey);

		if(VerifyUtils.isEmpty(this.jsonpCallback)) {
			// json模式
			return result;
		} else {
			// jsonp模式
			return this.jsonpCallback + "(" + JSONObject.toJSONString(result) + ")";
		}
	}

	/**
	 * 返回提示信息
	 */
	public Object prompt(String message) {
		Result result = setReturnInfo("prompt", message, null);

		if(VerifyUtils.isEmpty(this.jsonpCallback)) {
			// json模式
			return result;
		} else {
			// jsonp模式
			return this.jsonpCallback + "(" + JSONObject.toJSONString(result) + ")";
		}
	}

	/**
	 * 返回异常
	 */
	public Object exception(String message) {
		Result result = setReturnInfo("exception", message, null);

		if(VerifyUtils.isEmpty(this.jsonpCallback)) {
			// json模式
			return result;
		} else {
			// jsonp模式
			return this.jsonpCallback + "(" + JSONObject.toJSONString(result) + ")";
		}
	}
	
}
