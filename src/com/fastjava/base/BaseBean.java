package com.fastjava.base;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.fastjava.page.Page;
import com.fastjava.page.Sort;
import com.fastjava.util.VerifyUtils;

public class BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@JSONField(serialize = false)
	private String order;	//排序
	@JSONField(serialize = false)
	private String sort;	//排序方式 可传单值或列表
	
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
			if(VerifyUtils.isEmpty(order)) {
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
		this.sort = sort.toString();
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
	
}
