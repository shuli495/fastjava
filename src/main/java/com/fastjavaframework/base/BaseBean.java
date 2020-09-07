package com.fastjavaframework.base;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fastjavaframework.page.Page;
import com.fastjavaframework.page.OrderSort;
import com.fastjavaframework.util.VerifyUtils;

/**
 * @author wangshuli
 */
public class BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 排序
	 */
	@JSONField(serialize = false)
	private String orderBy;

	/**
	 * 排序方式 可传单值或列表
	 */
	@JSONField(serialize = false)
	private String orderSort;

	/**
	 * 返回几行数据 分页接口会查询总数 不需要总数使用此字段效率高
	 */
	@JSONField(serialize = false)
	private Integer rowNum;

	/**
	 * 分页
	 */
	@JSONField(serialize = false)
	private Page page;
	
	/**
	 * 单列排序
	 * @param orderBy
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		//默认ASC
		if(VerifyUtils.isEmpty(getOrderSort())) {
			setOrderSort(OrderSort.ASC);
		}
	}
	
	/**
	 * 多列排序
	 * @param orderBys
	 */
	public void setOrder(List<String> orderBys) {
		StringBuffer orderByList = new StringBuffer();
		for(String str : orderBys) {
			if(VerifyUtils.isEmpty(orderByList)) {
				orderByList.append(str);
			} else {
				orderByList.append(",").append(str);
			}
		}
		this.orderBy = orderByList.toString();
		
		if(VerifyUtils.isEmpty(getOrderSort())) {
			setOrderSort(OrderSort.ASC);
		}
	}

	public String getOrderSort() {
		return orderSort;
	}

	public void setOrderSort(OrderSort orderSort) {
		if(null != orderSort) {
			this.orderSort = orderSort.toString();
		}
	}

	public void setOrderSort(String orderSort) {
		if(VerifyUtils.isNotEmpty(orderSort) && orderSort.equalsIgnoreCase(OrderSort.DESC.toString())) {
			this.orderSort = orderSort.toString();
		}
	}

	public String getOrderBy() {
		return orderBy;
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

	@Override
	public String toString() {
		return "{\"orderBy\":\"" + this.orderBy
				+ "\",\"orderSort\":\"" + orderSort
				+ "\",\"rowNum\":" + rowNum
				+ ",\"page\":" + JSONObject.toJSONString(page) + "}";
	}

}
