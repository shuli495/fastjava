package com.fastjavaframework.base;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.fastjavaframework.page.Page;
import com.fastjavaframework.page.Sort;
import com.fastjavaframework.util.VerifyUtils;

public class BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@JSONField(serialize = false)
	private String order;	//排序
	@JSONField(serialize = false)
	private String sort;	//排序方式 可传单值或列表
	@JSONField(serialize = false)
	private Integer rowNum;	//返回几行数据 分页接口会查询总数 不需要总数使用此字段效率高
	
	@JSONField(serialize = false)
	private Page page;	//分页
	
	/**
	 * 单列排序
	 * @param order
	 */
	public void setOrder(String order) {
		this.order = order;
		//默认ASC
		if(VerifyUtils.isEmpty(getSort())) {
			setSort(Sort.ASC);
		}
	}
	
	/**
	 * 多列排序
	 * @param orders
	 */
	public void setOrder(List<String> orders) {
		StringBuffer orderList = new StringBuffer();
		for(String str : orders) {
			if(VerifyUtils.isEmpty(orderList)) {
				orderList.append(str);
			} else {
				orderList.append(",").append(str);
			}
		}
		this.order = orderList.toString();
		
		if(VerifyUtils.isEmpty(getSort())) {
			setSort(Sort.ASC);
		}
	}

	public String getSort() {
		return sort;
	}

	public void setSort(Sort sort) {
		if(null != sort) {
			this.sort = sort.toString();
		}
	}

	public String getOrder() {
		return order;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public Integer getRowNum() {
		return rowNum;
	}

	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}
}
