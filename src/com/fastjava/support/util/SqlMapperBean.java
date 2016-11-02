package com.fastjava.support.util;

import java.util.Set;

/**
 * mapper.xml属性
 */
public class SqlMapperBean {

	private String mapperName;	//mapperxml文件名
	private String nameSpace;	//命名空间
	private String refNameSpace;//关联的命名空间
	private String tableName;	//表明
	private Set<String> refTableName;	//关联表明
	
	public String getMapperName() {
		return mapperName;
	}
	public void setMapperName(String mapperName) {
		this.mapperName = mapperName;
	}
	public String getNameSpace() {
		return nameSpace;
	}
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
	public String getRefNameSpace() {
		return refNameSpace;
	}
	public void setRefNameSpace(String refNameSpace) {
		this.refNameSpace = refNameSpace;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Set<String> getRefTableName() {
		return refTableName;
	}
	public void setRefTableName(Set<String> refTableName) {
		this.refTableName = refTableName;
	}
	
}
