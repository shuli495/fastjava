package com.fastjava.response;

/**
 * 返回数据
 */
public class Result {

	private String status;	//success成功 prompt提示信息 exception异常
	private Object data;	//返回数据
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
}
