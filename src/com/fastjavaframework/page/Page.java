package com.fastjavaframework.page;

import org.apache.ibatis.session.RowBounds;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 分页对象
 */
public class Page extends RowBounds {

	private int totalCount;	//总记录数
	private int totalPage;	//总页数
	private int pageSize;	//条数
	private int pageNum;	//页数

	public Page() {
		super(0, 0);
		this.pageSize = 10;
		this.pageNum = 1;
	}
	
	public Page(int pageSize) {
		super(0, 0);
		this.pageSize = pageSize;
		this.pageNum = 1;
	}
	
	public Page(int pageNum, int pageSize) {
		super(0, 0);
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}
	
	/**
	 * 要查询的条数
	 */
	@Override
	@JSONField(serialize = false)
	public int getLimit() {
		return this.pageSize;
	}
	
	/**
	 * 查询起始序列
	 */
	@Override
	@JSONField(serialize = false)
	public int getOffset() {
		return this.pageSize * (this.pageNum - 1);
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	
}
