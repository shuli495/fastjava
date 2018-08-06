package com.fastjavaframework.page;

import java.util.List;

/**
 * 分页返回结果集
 *
 * @author wangshuli
 */
public class PageResult {

	/**
	 * 分页信息
	 */
	private Page page;

	/**
	 * 返回list<bean>
	 */
	private List<?> dataList;

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
