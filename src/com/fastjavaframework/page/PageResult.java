package com.fastjavaframework.page;

import java.util.List;

/**
 * 分页返回结果集
 */
public class PageResult {

	private Page page;	//分页信息
	private List<?> dataList;	//返回list<bean>

	public PageResult() {
	}
	
	public PageResult(Page page, List<?> dataList) {
		this.page = page;
		this.dataList = dataList;
	}
	
	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public List<?> getDataList() {
		return dataList;
	}

	public void setDataList(List<?> dataList) {
		this.dataList = dataList;
	}
	
}
