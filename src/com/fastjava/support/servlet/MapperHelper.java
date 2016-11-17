package com.fastjava.support.servlet;

import com.fastjava.exception.ThrowException;
import com.fastjava.support.util.Db;
import com.fastjava.util.FileUtil;
import com.fastjava.util.StringUtil;
import com.fastjava.util.VerifyUtils;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类生成器
 */
public class MapperHelper {
	
	/**
	 * 读取表信息
	 * @param ctx ApplicationContext
	 * @return mapperHelper中全局变量
	 */
	public Map<String,String> makeTableInfo(ApplicationContext ctx) {
		//表信息保存在js二维数组中
		StringBuffer div = new StringBuffer("[");
		try {
			DatabaseMetaData databaseMetaData = new Db().getDb(ctx);
			ResultSet tableRs = databaseMetaData.getTables(null, "%", "%", new String[] { "TABLE" });
			
			while (tableRs.next()) {
				String tableName = tableRs.getString("TABLE_NAME");
				String createTimeColumn = "";
				String updateTimeColumn = "";
				String delColumn = "";
				String delTimeColumn = "";

				ResultSet columnRs = databaseMetaData.getColumns(null, "%", tableName, "%");

				while (columnRs.next()) {
					String columName = toJavaName(columnRs.getString("COLUMN_NAME"));
					String columnType = columnRs.getString("TYPE_NAME");
					
					if(columName.indexOf("create") != -1 && (columnType.indexOf("DATE") != -1 || columnType.indexOf("TIME") != -1)) {
						createTimeColumn = columName;
					} else if(columName.indexOf("update") != -1 && (columnType.indexOf("DATE") != -1 || columnType.indexOf("TIME") != -1)) {
						updateTimeColumn = columName;
					} else if(columName.toLowerCase().indexOf("del") != -1) {
						switch (columnType) {
						case "DATE": delTimeColumn = columName; break;
						case "TIME": delTimeColumn = columName; break;
						case "DATETIME": delTimeColumn = columName; break;
						default: delColumn = columName; break;
					}
					}
				}
				
				if(!div.toString().equals("[")) {
					div.append(",");
				}
				
				div.append("[")
					.append(false).append(",")
					.append("\"").append(tableName).append("&")
					.append(createTimeColumn).append("&")
					.append(updateTimeColumn).append("&")
					.append(delColumn).append("&")
					.append(delTimeColumn).append("\"")
					.append("]");
			}
		} catch (SQLException e) {
			throw new ThrowException("读取数据库表错误：" + e.getMessage());
		}
		div.append("]");
		
		//保存在前端tableInfos全局属性中
		Map<String,String> replaceMap = new HashMap<>();
		replaceMap.put("tableInfos", div.toString());
		return replaceMap;
	}
	
	//读取文件包路径
	public Map<String,String> readPath(String projectPath) {
		String javaPath = "src"+File.separator+"main"+File.separator+"java"+File.separator;		//java包
		String resourcesPath = "src"+File.separator+"main"+File.separator+"resources"+File.separator;		//配置文件
		Map<String,String> replaceMap = new HashMap<>();
		
		//遍历项目子目录
		List<String> paths = FileUtil.iterator(projectPath, "", "path", true);
		for(String path : paths) {
			int index = 0;
			String rePath = "\"" + path.replaceAll("\\\\", "\\\\\\\\") + "\"";
			
			if((index=path.indexOf(javaPath)) != -1) {	//java类路径
				String nameSpace = "\""+path.substring(index+javaPath.length()).replaceAll("\\\\", ".").replaceAll(File.separator,".")+"\"";
				if(path.endsWith("action")) {
					replaceMap.put("actionNameSpace", nameSpace);
					replaceMap.put("actionPath", rePath);
				} else if(path.endsWith("service")) {
					replaceMap.put("serviceNameSpace", nameSpace);
					replaceMap.put("servicePath", rePath);
				} else if(path.endsWith("dao")) {
					replaceMap.put("daoNameSpace", nameSpace);
					replaceMap.put("daoPath", rePath);
				} else if(path.endsWith("bo")) {
					replaceMap.put("boNameSpace", nameSpace);
					replaceMap.put("boPath", rePath);
				}
			} else if((index=path.indexOf(resourcesPath)) != -1) {
				if(path.toLowerCase().endsWith("mapper")) {
					replaceMap.put("mapperPath", rePath);
				} else if(path.endsWith("ValidationMessages.properties")) {
					replaceMap.put("validationMessagesPath", rePath);
				}
			}
		}
		return replaceMap;
	}

	private String tableName = "";		//当前操作表
	private Map<String,Object> column = null;	//当前表的列集合
	private String tableJavaName = "";	//当前表java名
	private String tableClassName = "";	//当前表类名
	private String tableRemarks = "";	//当前表注释
	private String createTimeColumn = "";//当前表创建时间字段
	private String updateTimeColumn = "";//当前表修改时间字段
	private String delColumn = "";		//当前表逻辑删除字段
	private String delTimeColumn = "";	//当前表删除时间字段
	private String mapperName = "";		//mapperXML名
	private String boClassName = "";	//bo类名
	private String boNameSpace = "";	//bo命名空间(不包含类名)
	private String daoClassName = "";	//dao类名
	private String daoNameSpace = "";	//dao命名空间(不包含类名)
	private String serviceClassName = "";	//service类名
	private String serviceNameSpace = "";	//service命名空间(不包含类名)
	private String actionClassName = "";	//action类名
	private String actionNameSpace = "";	//action命名空间(不包含类名)
	private String propertiesPath = "";		//校验提示properties路径
	
	
	/**
	 * 生成文件
	 * @param ctx ApplicationContext
	 * @param params 前台参数
	 */
	public void createFile(ApplicationContext ctx, Map<String,String[]> params) {
		//前台选中的表
		String[] tableChked = params.get("tableChked");
		if(null == tableChked) {
			return;
		}
		
		//校验提示properties路径
		if(params.get("validationMessagesPath").length > 0) {
			propertiesPath = params.get("validationMessagesPath")[0];
		}
		
		DatabaseMetaData databaseMetaData = new Db().getDb(ctx);
		ResultSet tableRs = new Db().getTables(databaseMetaData);	//表集合
		

		//循环生成选中的表
		for(String tableInfo : tableChked) {
			String[] tableInfos = tableInfo.split("&");
			String tableName = tableInfos[0];
			if(tableInfos.length > 1) {
				this.createTimeColumn = tableInfos[1];
			}
			if(tableInfos.length > 2) {
				this.updateTimeColumn = tableInfos[2];
			}
			if(tableInfos.length > 3) {
				this.delColumn = tableInfos[3];
			}
			if(tableInfos.length > 4) {
				this.delTimeColumn = tableInfos[4];
			}
			
			this.tableName = tableName;	//当前操作的表，必须放在getClumn()前
			this.column = getClumn(databaseMetaData);
			this.tableJavaName = toJavaName(tableName);
			this.tableClassName = toClassName(tableName);
			this.tableRemarks = new Db().getTableRemarks(tableRs, tableName);
			this.boClassName = tableClassName + "BO";
			this.daoClassName = tableClassName + "Dao";
			this.serviceClassName = tableClassName + "Service";
			this.actionClassName = tableClassName + "Action";
			this.mapperName = tableJavaName + "Mapper";

			//设置命名空间
			if(isNeedCreate(params, "boNameSpace")) {
				this.boNameSpace = params.get("boNameSpace")[0];
			}
			if(isNeedCreate(params, "daoNameSpace")) {
				this.daoNameSpace = params.get("daoNameSpace")[0];
			}
			if(isNeedCreate(params, "serviceNameSpace")) {
				this.serviceNameSpace = params.get("serviceNameSpace")[0];
			}
			if(isNeedCreate(params, "actionNameSpace")) {
				this.actionNameSpace = params.get("actionNameSpace")[0];
			}
			
			//生成文件
			if(isNeedCreate(params, "mapperPath")) {
				createMapperXML(params.get("mapperPath")[0] + File.separator + mapperName + ".xml");
			}
			if(isNeedCreate(params, "boPath")) {
				createBo(params.get("boPath")[0] + File.separator + boClassName + ".java");
			}
			if(isNeedCreate(params, "daoPath")) {
				createDao(params.get("daoPath")[0] + File.separator + daoClassName + ".java");
			}
			if(isNeedCreate(params, "servicePath")) {
				createService(params.get("servicePath")[0] + File.separator + serviceClassName + ".java");
			}
			if(isNeedCreate(params, "actionPath")) {
				createAction(params.get("actionPath")[0] + File.separator + actionClassName + ".java");
			}
		}
	}
	
	/**
	 * 创建mapperXML
	 * @param path 生成路径
	 */
	private void createMapperXML(String path) {
		List<Map<String,String>> tableColumnList = columnList();
		
		StringBuffer resultMap = new StringBuffer();			//映射关系
		StringBuffer insertDbNames = new StringBuffer();		//插入数据库列","分割
		StringBuffer insertJavaNames = new StringBuffer();		//插入实体属性列#{name}
		StringBuffer insertBatchJavaNames = new StringBuffer();	//批量插如实体属性#{item.name}
		StringBuffer updateNames = new StringBuffer();			//更新列 数据库名=#{item.name}
		StringBuffer updatBeatcheNames = new StringBuffer();	//批量更新列 数据库名=#{item.name}
		StringBuffer selectByOr = new StringBuffer();			//or查询条件
		StringBuffer selectByAnd = new StringBuffer();			//and查询条件
		
		resultMap.append("\t<resultMap type=\"").append(boNameSpace + "." + boClassName)
				.append("\" id=\"").append(toJavaName(boClassName)).append("\">\n");
		
		//拼接 结果集、查询条件、插入列
		for(Map<String,String> tableColumn : tableColumnList) {
			//非第一列前加","
			if(!"".equals(insertDbNames.toString())) {
				insertDbNames.append(",");
				insertJavaNames.append(",");
				insertBatchJavaNames.append(",");
			}
			
			String dbName = columnDbName(tableColumn);		//数据库列名
			String javaName = columnJavaName(tableColumn);	//对应实体名

			//主键类型是String，需java传入
			if(columnDbPrimary().toLowerCase().equals(dbName.toLowerCase()) && "String".equals(columnJavaPrimaryType())) {
				//insert
				insertDbNames.append(dbName);
				insertJavaNames.append("#{" + javaName + "}");
				//batch insert
				insertBatchJavaNames.append("#{item." + javaName + "}");
			}
			
			//拼接主键列
			if(columnDbPrimary().toLowerCase().equals(dbName.toLowerCase())) {
				resultMap.append("\t\t<id column=\"").append(dbName).append("\" property=\"").append(javaName).append("\" />\n");
			} else {	//拼接非主键列
				//insert
				insertDbNames.append(dbName);
				insertJavaNames.append("#{" + javaName + "}");
				//batch insert
				insertBatchJavaNames.append("#{item." + javaName + "}");
				
				//update
				updateNames.append("\t\t\t<if test=\"").append(javaName).append(" != null\">\n")
						   .append("\t\t\t\t").append(dbName + "=#{" + javaName + "},\n")
						   .append("\t\t\t</if>\n");
				//updateBeatch
				updatBeatcheNames.append("\t\t\t\t<if test=\"").append(javaName).append(" != null\">\n")
								  .append("\t\t\t\t\t").append(dbName + "=#{item." + javaName + "},\n")
								  .append("\t\t\t\t</if>\n");
				
				//selectByOr
				selectByOr.append("\t\t\t<if test=\"").append(javaName).append(" != null\">\n")
						  .append("\t\t\t\tor ").append(dbName).append("=#{" + javaName + "}\n")
						  .append("\t\t\t</if>\n");
				//selectByAnd
				selectByAnd.append("\t\t\t<if test=\"").append(javaName).append(" != null\">\n")
						   .append("\t\t\t\tand ").append(dbName).append("=#{" + javaName + "}\n")
						   .append("\t\t\t</if>\n");
				
				resultMap.append("\t\t<result column=\"").append(dbName).append("\" property=\"").append(javaName).append("\" />\n");
			}
		}
		
		resultMap.append("\t</resultMap>");
		
		//逻辑删除
		StringBuffer delLogic = new StringBuffer();
		if(!"".equals(delColumn)) {
			//逻辑删除
			delLogic.append("\t<update id=\"deleteLogic\" parameterType=\"").append(toJavaName(columnJavaPrimaryType())).append("\">\n")
			   		.append("\t\tUPDATE ").append(tableName).append(" SET ").append(delTimeColumn).append("=now(),").append(delColumn).append("=#{").append(toJavaName(delColumn)).append("} WHERE ").append(columnDbPrimary()).append("=#{").append(columnJavaPrimary()).append("}\n")
			   		.append("\t</update>\n\n")
			
			   		//逻辑批量删除
			   		.append("\t<update id=\"deleteLogicBatch\" parameterType=\"java.util.List\">\n")
			   		.append("\t\t<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\";\">\n")
			   		.append("\t\t\tUPDATE ").append(tableName).append(" SET ").append(delTimeColumn).append("=now(),").append(delColumn).append("=#{item.").append(toJavaName(delColumn)).append("} WHERE ").append(columnDbPrimary()).append(" in #{item.id}\n")
			   		.append("\t\t</foreach>\n")
			   		.append("\t</update>\n\n");
		}
		
		StringBuffer code = new StringBuffer();
		code.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
			.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n")
			.append("<mapper namespace=\"").append(daoNameSpace).append(".").append(daoClassName).append("Mapper\">\n")
			.append(resultMap).append("\n\n")
			
			//插入
			.append("\t<insert id=\"insert\" parameterType=\"").append(boNameSpace + "." + boClassName).append("\" useGeneratedKeys=\"true\" keyProperty=\"").append(columnDbPrimary()).append("\">\n")
			.append("\t\tINSERT ").append(tableName).append("\n")
			.append("\t\t(").append(insertDbNames).append(")\n")
			.append("\t\tVALUES").append("\n")
			.append("\t\t(").append(insertJavaNames).append(")\n")
			.append("\t</insert>\n\n")
			
			//批量插入
			.append("\t<insert id=\"insertBatch\" parameterType=\"java.util.List\">\n")
			.append("\t\tINSERT ").append(tableName).append("\n")
			.append("\t\t(").append(insertDbNames).append(")\n")
			.append("\t\tVALUES").append("\n")
			.append("\t\t<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\">\n")
			.append("\t\t\t(").append(insertBatchJavaNames).append(")\n")
			.append("\t\t</foreach>\n")
			.append("\t</insert>\n\n")
			
			//更新
			.append("\t<update id=\"update\" parameterType=\"").append(boNameSpace + "." + boClassName).append("\">\n")
			.append("\t\tUPDATE ").append(tableName).append("\n")
			.append("\t\t<trim prefix=\"SET\" suffixOverrides=\",\">").append("\n")
			.append(updateNames)
			.append("\t\t</trim>\n")
			.append("\t\tWHERE ").append(columnDbPrimary()).append("=#{").append(columnJavaPrimary()).append("}\n")
			.append("\t</update>\n\n")
			
			//批量更新
			.append("\t<update id=\"updateBatch\" parameterType=\"java.util.List\">\n")
			.append("\t\t<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\";\">\n")
			.append("\t\t\tUPDATE ").append(tableName).append("\n")
			.append("\t\t\t<trim prefix=\"SET\" suffixOverrides=\",\">").append("\n")
			.append(updatBeatcheNames)
			.append("\t\t\t</trim>\n")
			.append("\t\t\tWHERE ").append(columnDbPrimary()).append("=#{").append(columnJavaPrimary()).append("}\n")
			.append("\t\t</foreach>\n")
			.append("\t</update>\n\n")
			
			//逻辑删除
			.append(delLogic)
			
			//物理删除
			.append("\t<delete id=\"delete\" parameterType=\"").append(columnJavaPrimaryType()).append("\">\n")
			.append("\t\tDELETE FROM ").append(tableName).append(" WHERE ").append(columnDbPrimary()).append("=#{").append(columnJavaPrimary()).append("}\n")
			.append("\t</delete>\n\n")
			
			//批量物理删除
			.append("\t<delete id=\"deleteBatch\" parameterType=\"java.util.List\">\n")
			.append("\t\tDELETE FROM ").append(tableName).append(" WHERE ").append(columnDbPrimary()).append(" IN\n")
			.append("\t\t<foreach item=\"item\" index=\"index\" collection=\"list\" open=\"(\" separator=\",\" close=\")\">\n")
			.append("\t\t\t#{item}\n")
			.append("\t\t</foreach>\n")
			.append("\t</delete>\n\n")
			
			//根据id查找
			.append("\t<select id=\"findById\" parameterType=\"").append(toJavaName(columnJavaPrimaryType())).append("\" resultMap=\"").append(toJavaName(boClassName)).append("\">\n")
			.append("\t\tSELECT\n")
			.append("\t\t").append(insertDbNames).append("\n")
			.append("\t\tFROM ").append(tableName).append("\n")
			.append("\t\tWHERE ").append(columnDbPrimary()).append("=#{").append(columnJavaPrimary()).append("}\n")
			.append("\t</select>\n\n")
			
			//or条件查找
			.append("\t<select id=\"queryByOr\" parameterType=\"").append(boNameSpace + "." + boClassName).append("\" resultMap=\"").append(toJavaName(boClassName)).append("\">\n")
			.append("\t\tSELECT\n")
			.append("\t\t").append(insertDbNames).append("\n")
			.append("\t\tFROM ").append(tableName).append("\n")
			.append("\t\t<where>\n")
			.append(selectByOr)
			.append("\t\t</where>\n")
			.append("\t</select>\n\n")
			
			//and条件查找
			.append("\t<select id=\"queryByAnd\" parameterType=\"").append(boNameSpace + "." + boClassName).append("\" resultMap=\"").append(toJavaName(boClassName)).append("\">\n")
			.append("\t\tSELECT\n")
			.append("\t\t").append(insertDbNames).append("\n")
			.append("\t\tFROM ").append(tableName).append("\n")
			.append("\t\t<where>\n")
			.append(selectByAnd)
			.append("\t\t</where>\n")
			.append("\t</select>\n\n")
			
			.append("</mapper>");
		
		//生成文件
		FileUtil.writeFile(code.toString(), new File(path));
	}

	/**
	 * 创建bo层
	 * @param path 生成路径
	 */
	private void createBo(String path) {
		StringBuffer attribute = new StringBuffer();
		StringBuffer getAndSet = new StringBuffer();
		StringBuffer importPack = new StringBuffer();
		
		List<Map<String,String>> tableColumnList = columnList();
		
		//遍历当前表的列
		for(Map<String,String> columnMap : tableColumnList) {
			String name = columnJavaName(columnMap);	//当前列驼峰格式名
			String type = columnType(columnMap);		//当前列数据类型
			String remarks = columnRemarks(columnMap);	//当前列注释
			Map<String, String> annotation = columnAnnotation(tableName, columnMap);	//当前列注解
			
			//导入包
			if("Date".equals(type) && importPack.toString().indexOf("java.util.Date") == -1) {
				importPack.append("import java.util.Date;\n");
			}
			if("BigDecimal".equals(type) && importPack.toString().indexOf("java.math.BigDecimal") == -1) {
				importPack.append("import java.math.BigDecimal;\n");
			}
			if(null != annotation.get("imports")) {
				for(String importName : annotation.get("imports").split(";")) {
					if(importPack.indexOf(importName) == -1) {
						importPack.append(importName).append(";\n");
					}
				}
			}
			
			//属性
			String annotations = annotation.get("annotations");
			attribute.append(null == annotations ? "" : annotations)
					.append("\tprivate ").append(type).append(" ").append(name).append(";\t").append(remarks).append("\n\n");
			
			//get、set方法
			getAndSet.append("\tpublic ").append(type).append(" get").append(toClassName(name)).append("() {\n")
				     .append("\t\treturn ").append(name).append(";\n")
				     .append("\t}\n\n")
				     .append("\tpublic void set").append(toClassName(name)).append("(").append(type).append(" ").append(name).append(") {\n")
				     .append("\t\tthis.").append(name).append(" = ").append(name).append(";\n")
				     .append("\t}\n\n");
		}
		
		//表注释信息
		StringBuffer remarks = new StringBuffer();
		if(!VerifyUtils.isEmpty(tableRemarks)) {
			remarks.append("/*\n")
				   .append(" * ").append(tableRemarks).append("\n")
				   .append(" */\n");
		}
		
		StringBuffer code = new StringBuffer();
		code.append("package ").append(boNameSpace).append(";\n\n")
			.append(("").equals(importPack.toString())?"":importPack.append("\n"))
			.append("import com.fastjava.base.BaseBean;\n\n")
			.append(remarks)
			.append("public class ").append(boClassName).append(" extends BaseBean {\n")
			.append("\tprivate static final long serialVersionUID = 1L;\n\n")
			.append(attribute).append("\n")
			.append(getAndSet)
			.append("}");

		//生成文件
		FileUtil.writeFile(code.toString(), new File(path));
	}

	/**
	 * 创建dao层
	 * @param path 生成路径
	 */
	private void createDao(String path) {
		StringBuffer code = new StringBuffer();
		
		//表注释信息
		StringBuffer remarks = new StringBuffer();
		if(!VerifyUtils.isEmpty(tableRemarks)) {
			remarks.append("/*\n")
				   .append(" * ").append(tableRemarks).append("\n")
				   .append(" */\n");
		}
		
		code.append("package ").append(daoNameSpace).append(";\n\n")
			.append("import org.springframework.stereotype.Repository;\n")
			.append("import com.fastjava.base.BaseDao;\n")
			.append("import ").append(boNameSpace).append(".").append(boClassName).append(";\n\n")
			.append(remarks)
			.append("@Repository\n")
			.append("public class ").append(daoClassName).append(" extends BaseDao<").append(boClassName).append("> {\n\n")
			.append("}");

		//生成文件
		FileUtil.writeFile(code.toString(), new File(path));
	}

	/**
	 * 创建service层
	 * @param path 生成路径
	 */
	private void createService(String path) {
		StringBuffer code = new StringBuffer();

//		List<Map<String,String>> tableColumnList = columnList();
		
//		//遍历当前表的列
//		StringBuffer upColumn = new StringBuffer();
//		for(Map<String,String> columnMap : tableColumnList) {
//			String name = toClassName(columnJavaName(columnMap));	//当前列明
//			String remarks = columnRemarks(columnMap);	//当前列注释
//			String type = columnType(columnMap);	//当前列数据类型
//
//			//不设置修改时间
//			if(name.toLowerCase().equals(updateTimeColumn.toLowerCase())) {
//				continue;
//			}
//			
//			//添加备注
//			if(!"".equals(remarks)) {
//				upColumn.append("\t\t").append(remarks).append("\n");
//			}
//
//			
//			//不能为null类型 不判断 直接设置值, 否则先判断是否为null
//			if("long".equals(type) || "float".equals(type) || "double".equals(type)) {
//				upColumn.append("\t\tdbBO.set").append(name).append("(upBO.get").append(name).append("());\n");
//			} else {
//				upColumn.append("\t\tif(null != upBO.get").append(name).append("()) {\n")
//						.append("\t\t\tdbBO.set").append(name).append("(upBO.get").append(name).append("());\n")
//						.append("\t\t}\n");
//			}
//		}
//		
//		//修改时间
//		String importDate = "";
//		if(!"".equals(updateTimeColumn)) {
//			upColumn.append("\n\t\t//修改时间\n")
//					.append("\t\tdbBO.set").append(toClassName(updateTimeColumn)).append("(new Date());\n\n");
//			
//			importDate = "import java.util.Date;\n";
//		}
		
		//表注释信息
		StringBuffer remarks = new StringBuffer();
		if(!VerifyUtils.isEmpty(tableRemarks)) {
			remarks.append("/*\n")
				   .append(" * ").append(tableRemarks).append("\n")
				   .append(" */\n");
		}
		
		code.append("package ").append(serviceNameSpace).append(";\n\n")
//			.append("import java.util.ArrayList;\n")
//			.append("import java.util.List;\n")
//			.append(importDate)
			.append("import org.springframework.stereotype.Service;\n")
			.append("import com.fastjava.base.BaseService;\n")
			.append("import ").append(daoNameSpace).append(".").append(daoClassName).append(";\n")
			.append("import ").append(boNameSpace).append(".").append(boClassName).append(";\n\n")
			.append(remarks)
			.append("@Service\n")
			.append("public class ").append(serviceClassName).append(" extends BaseService<").append(daoClassName).append(",").append(boClassName).append("> {\n\n")
			
//			//更新
//			.append("\t/**\n")
//			.append("\t * 更新\n")
//			.append("\t */\n")
//			.append("\tpublic int update(").append(boClassName).append(" bo) {\n")
//			.append("\t\t//设置修改值\n")
//			.append("\t\t").append(boClassName).append(" upBO = this.setUpdateVlaue(super.find(bo.getId()), bo);\n\n")
//			.append("\t\t//更新\n")
//			.append("\t\treturn super.update(upBO);\n")
//			.append("\t}\n\n")
//			
//			//批量更新
//			.append("\t/**\n")
//			.append("\t * 批量更新\n")
//			.append("\t */\n")
//			.append("\tpublic int updateBatch(List<").append(boClassName).append("> boList) {\n")
//			.append("\t\tList<").append(boClassName).append("> upList = new ArrayList<>();\n\n")
//			.append("\t\t//设置修改值\n")
//			.append("\t\tfor(").append(boClassName).append(" bo : boList) {\n")
//			.append("\t\t\tupList.add(this.setUpdateVlaue(super.find(bo.getId()), bo));\n")
//			.append("\t\t}\n\n")
//			.append("\t\t//更新\n")
//			.append("\t\treturn super.updateBatch(boList);\n")
//			.append("\t}\n\n")
//			
//			.append("\t/**\n")
//			.append("\t * 设置修改的属性(不为null为修改)\n")
//			.append("\t * @param dbBO 库中最新bo\n")
//			.append("\t * @param upBo	修改的bo\n")
//			.append("\t * @return 修改后的bo\n")
//			.append("\t */\n")
//			.append("\tprivate ").append(boClassName).append(" setUpdateVlaue(").append(boClassName).append(" dbBO, ").append(boClassName).append(" upBO) {\n")
//			.append(upColumn)
//			.append("\t\treturn dbBO;\n")
//			.append("\t}\n\n")
			
			.append("}");

		//生成文件
		FileUtil.writeFile(code.toString(), new File(path));
	}

	/**
	 * 创建action层
	 * @param path 生成路径
	 */
	private void createAction(String path) {
		StringBuffer code = new StringBuffer();
		
		String importDate = "";
		
		//创建设置项
		StringBuffer createSet = new StringBuffer();
		//uuid
		createSet.append("String".equals(columnJavaPrimaryType())?"\t\tbo.set"+toClassName(columnJavaPrimary())+"(UUID.uuid());\n\n":"");
		//创建时间
		if(!"".equals(createTimeColumn)) {
			createSet.append("\t\t//创建时间\n")
					.append("\t\tbo.set").append(toClassName(createTimeColumn)).append("(new Date());\n\n");
			
			importDate = "import java.util.Date;\n";
		}
		
		//批量创建设置项
		StringBuffer createBatchSet = new StringBuffer();
		createBatchSet.append("\t\tif(boArry.length == 0) {\n")
					.append("\t\t\tthrow new ThrowPrompt(\"无创建内容！\");\n")
					.append("\t\t}\n\n")
					.append("\t\tList<").append(boClassName).append("> boList = Arrays.asList(boArry);\n\n");
		
		if(!"".equals(createTimeColumn) || "String".equals(columnJavaPrimaryType())) {
			createBatchSet.append("\t\tfor(").append(boClassName).append(" bo : boList) {\n");
			
			//uuid
			if ("String".equals(columnJavaPrimaryType())) {
				createBatchSet.append("String".equals(columnJavaPrimaryType()) ? "\t\t\tbo.set"
								+ toClassName(columnJavaPrimary()) + "(UUID.uuid());\n" : "");
			}
			
			//创建时间
			if(!"".equals(createTimeColumn)) {
				createBatchSet.append("\t\t\t//创建时间\n")
						.append("\t\t\tbo.set").append(toClassName(createTimeColumn)).append("(new Date());\n");
				
				if(importDate.indexOf("java.util.Date") == -1) {
					importDate = "import java.util.Date;\n";
				}
			}
			
			createBatchSet.append("\t\t}\n\n");
		}
		
		//批量修改设置
		StringBuffer updateBatchSet = new StringBuffer();
		updateBatchSet.append("\t\tif(boArry.length == 0) {\n")
					.append("\t\t\tthrow new ThrowPrompt(\"无修改内容！\");\n")
					.append("\t\t}\n\n")
					.append("\t\tList<").append(boClassName).append("> boList = Arrays.asList(boArry);\n\n");
		
		//批量删除设置
		StringBuffer delBatchSet = new StringBuffer();
		delBatchSet.append("\t\tif(idList.size() == 0) {\n")
				.append("\t\t\tthrow new ThrowPrompt(\"无删除内容！\");\n")
				.append("\t\t}\n\n");
		
		
		//逻辑删除
		StringBuffer delLogic = new StringBuffer();
		if(!"".equals(delColumn)) {
			//删除列数据类型
			String type = "";
			List<Map<String,String>> tableColumnList = columnList();
			//遍历当前表的列
			for(Map<String,String> columnMap : tableColumnList) {
				String name = columnJavaName(columnMap);//当前列驼峰格式名
				if(name.equals(toJavaName(delColumn))) {
					type = columnType(columnMap);
				}
			}
			
			StringBuffer delTime = new StringBuffer();
			if(!"".equals(delTimeColumn)) {
				delTime.append("\t\tbo.set").append(toClassName(delTimeColumn)).append("(new Date());\t//删除时间\n\n");
			}
			
			//删除标记设置
			StringBuffer setFlag = new StringBuffer();
			switch (type) {
				case "Boolean": setFlag.append("true"); break;
				case "Integer": setFlag.append("1"); break;
				case "Int": setFlag.append("1"); break;
				default: setFlag.append("\"1\""); break;
			}
			
			delLogic.append("\t/**\n")
					.append("\t * 逻辑删除\n")
					.append("\t */\n")
					.append("\t@RequestMapping(value=\"/logic/{id}\",method=RequestMethod.DELETE)\n")
					.append("\tpublic Result deleteLogic(@PathVariable ").append(columnJavaPrimaryType()).append(" id) {\n")
					.append("\t\t").append(boClassName).append(" bo = this.service.find(id);\n")
					.append("\t\tbo.set").append(toClassName(columnJavaPrimary())).append("(id);\n")
					.append("\t\tbo.set").append(toClassName(delColumn)).append("(").append(setFlag).append(");\t//删除标记\n")
					.append(delTime)
					.append("\t\tthis.service.baseDeleteLogic(bo);\n")
					.append("\t\treturn success();\n")
					.append("\t}\n\n")
					
					.append("\t/**\n")
					.append("\t * 批量逻辑删除\n")
					.append("\t */\n")
					.append("\t@RequestMapping(value=\"/logic/deleteBatch\",method=RequestMethod.DELETE)\n")
					.append("\tpublic Result deleteLogicBatch(@RequestBody List<").append(columnJavaPrimaryType()).append("> idList) {\n")
					.append(delBatchSet)
					.append("\t\t//设置id、删除标记\n")
					.append("\t\tList<").append(boClassName).append("> boList = new ArrayList<>();\n")
					.append("\t\tfor(").append(columnJavaPrimaryType()).append(" id : idList) {\n")
					.append("\t\t\t").append(boClassName).append(" bo = this.service.find(id);\n")
					.append("\t\t\tbo.set").append(toClassName(columnJavaPrimary())).append("(id);\n")
					.append("\t\t\tbo.set").append(toClassName(delColumn)).append("(").append(setFlag).append(");\t//删除标记\n")
					.append("\t").append(delTime)
					.append("\t\t\tboList.add(bo);\n")
					.append("\t\t}\n\n")
					.append("\t\tthis.service.baseDeleteLogicBatch(boList);\n")
					.append("\t\treturn success();\n")
					.append("\t}\n\n");
		}

		
		//list查询条件
		StringBuffer importRequestParam = new StringBuffer();
		StringBuffer queryCondition = new StringBuffer();	//参数
		StringBuffer queryConditionSet = new StringBuffer();	//set参数
		List<Map<String,String>> tableColumnList = columnList();

		queryCondition.append("@RequestParam(required = false) Integer pageSize, ")
				      .append("@RequestParam(required = false) Integer pageNum,\n")
					  .append("\t\t\t\t\t\t@RequestParam(required = false) String sort, ")
					  .append("@RequestParam(required = false) String order,");

		int nIndex = 1;	//参数换行序列
		for(int i=0; i<tableColumnList.size(); i++) {
			Map<String,String> columnMap = tableColumnList.get(i);

			String columName = columnJavaName(columnMap);	//当前列明
			String columRemarks = columnRemarks(columnMap);	//当前列注释
			String columType = columnType(columnMap);	//当前列数据类型

			//跳过主键
			if(columnJavaPrimary().toLowerCase().equals(columName.toLowerCase())) {
				continue;
			}
			
			//不能为null类型 不判断 直接设置值, 否则先判断是否为null
			if(!"long".equals(columType) && !"float".equals(columType) && !"double".equals(columType) && !"Date".equals(columType)) {

				//参数换行
				if(nIndex%2 == 1) {	//2个换行
					queryCondition.append("\n\t\t\t\t\t\t");
				} else {
					queryCondition.append(" ");
				}
				nIndex++;
				
				queryCondition.append("@RequestParam(required = false) ").append(columType).append(" ").append(columName);

				//参数逗号
				if(i != tableColumnList.size() - 1) {
					queryCondition.append(",");
				}


				//添加备注
				if(!"".equals(columRemarks)) {
					queryConditionSet.append("\t\t").append(columRemarks).append("\n");
				}
				
				//set参数
				queryConditionSet.append("\t\tif(null != ").append(columName).append(") {\n")
							.append("\t\t\tbo.set").append(toClassName(columName)).append("(").append(columName).append(");\n")
							.append("\t\t}\n");
				
				if("".equals(importRequestParam.toString())) {
					importRequestParam.append("import org.springframework.web.bind.annotation.RequestParam;\n");
				}
			}
		
		}
		
		//表注释信息
		StringBuffer remarks = new StringBuffer();
		if(!VerifyUtils.isEmpty(tableRemarks)) {
			remarks.append("/*\n")
				   .append(" * ").append(tableRemarks).append("\n")
				   .append(" */\n");
		}
		
		code.append("package ").append(actionNameSpace).append(";\n\n")
			.append(importDate)
			.append(!"".equals(delColumn)?"import java.util.ArrayList;\n":"")
			.append("import java.util.Arrays;\n\n")
			.append("import java.util.List;\n\n")
			.append(importRequestParam)
			.append("import org.springframework.web.bind.annotation.RestController;\n")
			.append("import org.springframework.web.bind.annotation.PathVariable;\n")
			.append("import org.springframework.web.bind.annotation.RequestBody;\n")
			.append("import org.springframework.web.bind.annotation.RequestMapping;\n")
			.append("import org.springframework.web.bind.annotation.RequestMethod;\n\n")
			.append("String".equals(columnJavaPrimaryType())?"import com.fastjava.util.UUID;\n":"")
			.append("import com.fastjava.page.Page;\n")
			.append("import com.fastjava.base.BaseAction;\n")
			.append("import com.fastjava.exception.ThrowPrompt;\n")
			.append("import com.fastjava.response.Result;\n")
			.append("import ").append(boNameSpace).append(".").append(boClassName).append(";\n")
			.append("import ").append(serviceNameSpace).append(".").append(serviceClassName).append(";\n\n")
			.append(remarks)
			.append("@RestController\n")
			.append("@RequestMapping(value=\"/").append(tableJavaName).append("\")\n")
			.append("public class ").append(actionClassName).append(" extends BaseAction<").append(serviceClassName).append("> {\n\n")
			
			//create
			.append("\t/**\n")
			.append("\t * 创建\n")
			.append("\t */\n")
			.append("\t@RequestMapping(method=RequestMethod.POST)\n")
			.append("\tpublic Result create(@RequestBody ").append(boClassName).append(" bo) {\n")
			.append(createSet)
			.append("\t\tthis.service.baseInsert(bo);\n")
			.append("\t\treturn success(bo.get").append(toClassName(columnJavaPrimary())).append("()").append(");\n")
			.append("\t}\n\n")
			
			//createBatch
			.append("\t/**\n")
			.append("\t * 批量创建\n")
			.append("\t */\n")
			.append("\t@RequestMapping(value=\"/batch\",method=RequestMethod.POST)\n")
			.append("\tpublic Result createBatch(@RequestBody ").append(boClassName).append("[] boArry) {\n")
			.append(createBatchSet)
			.append("\t\tthis.service.baseInsertBatch(boList);\n")
			.append("\t\treturn success();\n")
			.append("\t}\n\n")
			
			//update
			.append("\t/**\n")
			.append("\t * 更新\n")
			.append("\t */\n")
			.append("\t@RequestMapping(method=RequestMethod.PUT)\n")
			.append("\tpublic Result update(@RequestBody ").append(boClassName).append(" bo) {\n")
			.append("\t\tthis.service.baseUpdate(bo);\n")
			.append("\t\treturn success();\n")
			.append("\t}\n\n")
			
			//updateBatch
			.append("\t/**\n")
			.append("\t * 批量更新\n")
			.append("\t */\n")
			.append("\t@RequestMapping(value=\"/batch\",method=RequestMethod.PUT)\n")
			.append("\tpublic Result updateBatch(@RequestBody ").append(boClassName).append("[] boArry) {\n")
			.append(updateBatchSet)
			.append("\t\tthis.service.baseUpdateBatch(boList);\n")
			.append("\t\treturn success();\n")
			.append("\t}\n\n")
			
			//逻辑删除
			.append(delLogic)
			
			//物理删除
			.append("\t/**\n")
			.append("\t * 物理删除\n")
			.append("\t */\n")
			.append("\t@RequestMapping(value=\"/{id}\",method=RequestMethod.DELETE)\n")
			.append("\tpublic Result delete(@PathVariable ").append(columnJavaPrimaryType()).append(" id) {\n")
			.append("\t\tthis.service.baseDelete(id);\n")
			.append("\t\treturn success();\n")
			.append("\t}\n\n")
			
			//批量物理删除
			.append("\t/**\n")
			.append("\t * 批量物理删除\n")
			.append("\t */\n")
			.append("\t@RequestMapping(value=\"/batch\",method=RequestMethod.DELETE)\n")
			.append("\tpublic Result deleteBatch(@RequestBody List<").append(columnJavaPrimaryType()).append("> idList) {\n")
			.append(delBatchSet)
			.append("\t\tthis.service.baseDeleteBatch(idList);\n")
			.append("\t\treturn success();\n")
			.append("\t}\n\n")
			
			//findById
			.append("\t/**\n")
			.append("\t * id查询详情\n")
			.append("\t */\n")
			.append("\t@RequestMapping(value=\"/{id}\",method=RequestMethod.GET)\n")
			.append("\tpublic Result findById(@PathVariable ").append(columnJavaPrimaryType()).append(" id) {\n")
			.append("\t\treturn success(this.service.baseFind(id));\n")
			.append("\t}\n\n")
			
			//query
			.append("\t/**\n")
			.append("\t * 列表查询 and条件\n")
			.append("\t */\n")
			.append("\t@RequestMapping(method=RequestMethod.GET)\n")
			.append("\tpublic Result query(").append(queryCondition).append(") {\n")
			.append("\t\t").append(boClassName).append(" bo = new ").append(boClassName).append("();\n")
			.append(queryConditionSet).append("\n")
			.append("\t\tif(pageSize != null && pageNum != null && pageSize != 0 && pageNum != 0) {	//分页查询\n")
			.append("\t\t\tPage page = new Page();\n")
			.append("\t\t\tpage.setPageSize(pageSize);\n")
			.append("\t\t\tpage.setPageNum(pageNum);\n")
			.append("\t\t\tbo.setPage(page);\n\n")
			.append("\t\t\treturn success(this.service.baseQueryPageByAnd(bo));\n")
			.append("\t\t} else {	//列表查询\n")
			.append("\t\t\treturn success(this.service.baseQueryByAnd(bo));\n")
			.append("\t\t}\n")
			.append("\t}\n\n")
			
			.append("}");

		//生成文件
		FileUtil.writeFile(code.toString(), new File(path));
	}
	
	/**
	 * 数据库名转java名(驼峰命名)
	 * eg:base_user to baseUser
	 * @param dbName
	 * @return javaName
	 */
	private String toJavaName(String dbName) {
		if(VerifyUtils.isEmpty(dbName)) {
			return dbName;
		}
		
		String[] names = dbName.split("_");
		
		StringBuffer javaName = new StringBuffer();
		for(String name : names) {
			if("".equals(javaName.toString())) {
				javaName.append(name.substring(0, 1).toLowerCase());
				if(name.length() > 1) {
					javaName.append(name.substring(1));
				}
			} else {
				javaName.append(name.substring(0, 1).toUpperCase());
				if(name.length() > 1) {
					javaName.append(name.substring(1));
				}
			}
		}
		
		return javaName.toString();
	}

	/**
	 * 数据库名转java类名
	 * eg:base_user to BaseUser
	 * @param dbName
	 * @return className
	 */
	private String toClassName(String dbName) {
		if(VerifyUtils.isEmpty(dbName)) {
			return dbName;
		}
		
		String[] names = dbName.split("_");
		
		StringBuffer javaName = new StringBuffer();
		for(String name : names) {
			javaName.append(name.substring(0, 1).toUpperCase());
			if(name.length() > 1) {
				javaName.append(name.substring(1));
			}
		}
		
		return javaName.toString();
	}
	
	/**
	 * 获取当前表的列
	 * @param databaseMetaData
	 * @return 列信息
	 *         key:primary(String)
	 *         value:主键列名(String)
	 *         
	 *         key:column(String)
	 *         value:列信息(Map:dbName列名,javaName对应的驼峰格式名,remarks备注,type数据类型)包含主键
	 */
	private Map<String,Object> getClumn(DatabaseMetaData databaseMetaData) {
		Map<String,Object> returnMap = new HashMap<>();
		try {
			ResultSet columnRs = databaseMetaData.getColumns(null, "%", tableName, "%");
			ResultSet primaryRs = databaseMetaData.getPrimaryKeys(null, "%", tableName);

			//主键
			while (primaryRs.next()) {
				returnMap.put("primary", primaryRs.getString("COLUMN_NAME").toLowerCase());
				break;
			}

			//列 包含主键
			List<Map<String,String>> columnList = new ArrayList<>();
			while (columnRs.next()) {
				Map<String,String> columnMap = new HashMap<>();
				String name = columnRs.getString("COLUMN_NAME").toLowerCase();
				columnMap.put("dbName", name);
				columnMap.put("javaName", toJavaName(name));
				columnMap.put("remarks", columnRs.getString("REMARKS"));
				columnMap.put("type", db2JavaType(columnRs.getString("TYPE_NAME")));
				columnMap.put("isNullAble", columnRs.getString("IS_NULLABLE"));
				columnMap.put("size", columnRs.getString("COLUMN_SIZE"));	//长度
				columnMap.put("decimal", columnRs.getString("DECIMAL_DIGITS"));	//精度
				
				String isPK = "false";	//是否主键
				String isAutoIncrement = "false";	//是否自增
				if(returnMap.get("primary").equals(name)) {
					isPK = "true";
					isAutoIncrement = columnRs.getString("IS_AUTOINCREMENT");
				}
				columnMap.put("isPK", isPK);
				columnMap.put("isAutoIncrement", isAutoIncrement);
				
				columnList.add(columnMap);
			}
			returnMap.put("column", columnList);
		} catch(Exception e) {
			
		}
		return returnMap;
	}
	
	/**
	 * 获取当前表主键名
	 * @return 主键列名
	 */
	private String columnDbPrimary() {
		return null == this.column.get("primary")?"":this.column.get("primary").toString();
	}

	/**
	 * 获取当前表主键名(驼峰格式)
	 * @return 主键列名
	 */
	private String columnJavaPrimary() {
		return toJavaName(columnDbPrimary());
	}
	
	/**
	 * 获取当前表主键的数据类型
	 * @return
	 */
	private String columnJavaPrimaryType() {
		List<Map<String,String>> columnList = (List<Map<String,String>>)column.get("column");
		if(null == columnList || columnList.size() == 0) {
			return "";
		}
		
		for(Map<String,String> map :columnList) {
			if(map.get("dbName").equals(columnDbPrimary())) {
				return map.get("type");
			}
		}
		return "";
	}
	
	/**
	 * 当前表列信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String,String>> columnList() {
		if(null == this.column.get("column")) {
			return new ArrayList<Map<String,String>>();
		} else {
			return (List<Map<String,String>>)this.column.get("column");
		}
	}
	
	/**
	 * 当前操作列的字段名
	 * @param map 列信息
	 * @return 字段名
	 */
	private String columnDbName(Map<String,String> map) {
		return null == map.get("dbName")?"":map.get("dbName").toUpperCase();
	}

	/**
	 * 当前操作列的字段名(驼峰格式)
	 * @param map 列信息
	 * @return 字段名
	 */
	private String columnJavaName(Map<String,String> map) {
		return null == map.get("javaName")?"":map.get("javaName");
	}
	
	/**
	 * 当前列java数据类型
	 * @param map 列信息
	 * @return 对应的java数据类型
	 */
	private String columnType(Map<String,String> map) {
		return map.get("type");
	}
	
	/**
	 * 当前列注解
	 * @param map 列信息
	 * @return map imports 导入包 annotations注解
	 */
	private Map<String, String> columnAnnotation(String tableName, Map<String,String> map) {
		Map<String, String> reMap = new HashMap<>();
		
		//不设置主键
		if("true".equals(map.get("isPK"))) {
			return reMap;
		}
		
		String name = map.get("javaName");
		String type = map.get("type");
		String size = map.get("size");
		String remarks = null == map.get("remarks")
						|| "".equals(map.get("remarks")) 
						? name : map.get("remarks");

		StringBuffer imports = new StringBuffer();
		StringBuffer annotations = new StringBuffer();
		
		//校验提示信息
		String messageKeyHead = tableName + "." + name;
		String messageKey = "";
		String messageValue = "";
		
		//@NotNull
		if(!"yes".equals(map.get("isNullAble").toLowerCase())) {
			messageKey = messageKeyHead + ".null";
			messageValue = "请填写" + remarks;
			
			annotations.append("\t@NotNull(message=\"{").append(messageKey).append("}\")\n");
			
			imports.append("import javax.validation.constraints.NotNull;");
			
			//设置校验提示信息
			setMessageProperties(messageKey, messageValue);
		}

		//@Max or @Digits
		if (type.equals("Integer") || type.equals("long")
				|| type.equals("float") || type.equals("double")
				|| type.equals("Decimal")) {

			String decimal = map.get("decimal");
			
			//@Digits
			if(null != decimal && !"".equals(decimal) && !"0".equals(decimal)) {
				messageKey = messageKeyHead + ".digits";
				messageValue = remarks + "格式为{digits}位数字，{fraction}位小数";
				
				annotations.append("\t@Digits(integer=").append(size).append(", fraction=").append(decimal)
						.append(", message=\"{").append(messageKey).append("}\")\n");
				
				imports.append("import javax.validation.constraints.Digits;");
			} else {	//@Max
				messageKey = messageKeyHead + ".max";
				messageValue = remarks + "最大值为{value}";
				
				//设置最大值
				String maxNum = "1";
				for(int i=0; i<Integer.valueOf(size); i++) {
					if(i == 8) {
						break;
					}
					maxNum += "0";
				}
				
				annotations.append("\t@Max(value=").append(Long.valueOf(maxNum) - 1)
						.append(", message=\"{").append(messageKey).append("}\")\n");
				
				imports.append("import javax.validation.constraints.Max;");
			}

			//设置校验提示信息
			setMessageProperties(messageKey, messageValue);
		} else if(type.equals("String")) {	//@Size
			messageKey = messageKeyHead + ".size";
			messageValue = remarks + "最大长度为{max}";
			
			annotations.append("\t@Size(max=").append(size).append(", min=0, message=\"{")
					.append(messageKey).append("}\")\n");
			
			imports.append("import javax.validation.constraints.Size;");

			//设置校验提示信息
			setMessageProperties(messageKey, messageValue);
		}
		
		reMap.put("imports", imports.toString());
		reMap.put("annotations", annotations.toString());
		
		return reMap;
	}
	
	/**
	 * 设置properties 校验提示信息
	 * @param messageKey
	 * @param messageValue
	 */
	private void setMessageProperties(String messageKey, String messageValue) {
		if(VerifyUtils.isEmpty(messageKey) || VerifyUtils.isEmpty(propertiesPath)) {
			return;
		}
		
		messageValue = StringUtil.toUnicode(messageValue);
		
		List<String> properties = FileUtil.readLine(propertiesPath);
		
		//修改已经存在的message
		boolean isAdd = false;
		for (int i = 0; i < properties.size(); i++) {
			if(properties.get(i).indexOf(messageKey.toString()) != -1) {
				properties.remove(i);
				properties.add(i, messageKey + "=" + messageValue);
				isAdd = true;
				break;
			}
		}
		
		//不存在则新增
		if(!isAdd) {
			boolean isAddTable = false;
			
			//已存在该表属性，在该表尾部增加
			for(int i = properties.size() - 1; i >= 0; i--) {
				if(properties.get(i).indexOf(tableName + ".") != -1) {
					properties.add(i + 1, messageKey + "=" + messageValue);
					isAddTable = true;
					break;
				}
			}	
			
			//不存在该表属性，在文档尾部增加
			if(!isAddTable) {
				properties.add("\n" + messageKey + "=" + messageValue);
			}
		}

		//拼接文档
		StringBuffer messages = new StringBuffer();
		for (int i = 0; i < properties.size(); i++) {
			messages.append(properties.get(i)).append("\n");
		}
		
		//写入文件
		FileUtil.writeFile(messages.toString(), new File(propertiesPath), "ISO-8859-1");
	}
	
	/**
	 * 数据库类型转java类型
	 * @param type	数据库数据类型
	 * @return	java数据类型
	 */
	private String db2JavaType(String type) {
		switch (type) {
			case "INT" : return "Integer";
			case "BIGINT" : return "long";
			case "FLOAT" : return "float";
			case "DOUBLE" : return "double";
			case "DECIMAL" : return "BigDecimal";
			case "BIT" : return "Boolean";
			case "TINYINT" : return "Boolean";
			case "BLOB" : return "byte[]";
			case "TIME" : return "Date";
			case "DATE" : return "Date";
			case "DATETIME" : return "Date";
			default : return "String";
		}
	}
	
	/**
	 * 当前列备注信息
	 * @param map 列信息
	 * @return 备注
	 */
	private String columnRemarks(Map<String,String> map) {
		return null == map.get("remarks") || "".equals(map.get("remarks").toString())?"":"//" + map.get("remarks");
	}
	
	/**
	 * 前台是否返回对应的路径，不返回则不生成
	 * @param params    前台返回参数
	 * @param paramName 参数名
	 * @return 是否存在
	 */
	private boolean isNeedCreate(Map<String,String[]> params, String paramName) {
		if(params.get(paramName).length > 0 && !"".equals(params.get(paramName)[0])) {
			return true;
		}
		
		return false;
	}
	
}
