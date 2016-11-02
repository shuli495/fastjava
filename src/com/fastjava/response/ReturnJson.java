package com.fastjava.response;

import java.util.HashMap;
import java.util.Map;

import com.fastjava.exception.ThrowException;
import com.fastjava.exception.ThrowPrompt;
import com.fastjava.util.VerifyUtils;

/**
 * 返回前台json格式数据
 */
public class ReturnJson {
	
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
			data = reObj.toString().replace(ThrowPrompt.RETRUN_PROMPT, "");
		} else if("exception".equals(type)) {	//异常
			if(!VerifyUtils.isEmpty(reObj) && reObj.toString().startsWith(ThrowException.RETRUN_EXCEPTION)) {
				data = reObj.toString().replace(ThrowException.RETRUN_EXCEPTION, "");
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
	public Result success() {
		return setReturnInfo("success", "success", null);
	}
	
	/**
	 * 返回Object json
	 * @param returnObj
	 * @return
	 */
	public Result success(Object returnObj) {
		return setReturnInfo("success", returnObj, null);
	}
	
	/**
	 * 返回string json
	 * @param returnStr
	 * @return
	 */
	public Result success(String returnStr) {
		return setReturnInfo("success", returnStr, null);
	}
	
	/**
	 * 返回key-value json
	 * @param returnStrKey
	 * @param returnStrVal
	 * @return
	 */
	public Result success(String returnStrKey, Object returnStrVal) {
		return setReturnInfo("success", returnStrVal, returnStrKey);
	}

	/**
	 * 返回提示信息
	 */
	public Result prompt(String message) {
		return setReturnInfo("prompt", message, null);
	}

	/**
	 * 返回异常
	 */
	public Result exception(String message) {
		return setReturnInfo("exception", message, null);
	}
	
}
