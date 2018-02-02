package com.fastjavaframework.support.servlet;

import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.support.util.Db;
import com.fastjavaframework.util.FileUtil;
import com.fastjavaframework.util.StringUtil;
import com.fastjavaframework.util.VerifyUtils;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类生成器
 */
@SuppressWarnings("unchecked")
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
				
				if(!"[".equals(div.toString())) {
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

	/**
	 * 读取文件包路径
	 * @param projectPath
	 * @return
     */
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
				String nameSpace = "\""+path.substring(index+javaPath.length()).replaceAll("\\\\", ".").replaceAll(Matcher.quoteReplacement(File.separator),".")+"\"";
				if(path.endsWith("action") || path.endsWith("control") || path.endsWith("controller")) {
					replaceMap.put("controllerNameSpace", nameSpace);
					replaceMap.put("controllerPath", rePath);
				} else if(path.endsWith("service")) {
					replaceMap.put("serviceNameSpace", nameSpace);
					replaceMap.put("servicePath", rePath);
				} else if(path.endsWith("dao")) {
					replaceMap.put("daoNameSpace", nameSpace);
					replaceMap.put("daoPath", rePath);
				} else if(path.endsWith("bo")) {
					replaceMap.put("boNameSpace", nameSpace);
					replaceMap.put("boPath", rePath);
				} else if(path.endsWith("vo")) {
					replaceMap.put("voNameSpace", nameSpace);
					replaceMap.put("voPath", rePath);
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
	private List<Map<String,Object>> addColumns = new ArrayList<>();	//需要增加的列
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
	private String voClassName = "";	//vo类名
	private String voNameSpace = "";	//vo命名空间(不包含类名)
	private String daoClassName = "";	//dao类名
	private String daoNameSpace = "";	//dao命名空间(不包含类名)
	private String serviceClassName = "";	//service类名
	private String serviceNameSpace = "";	//service命名空间(不包含类名)
	private String controllerClassName = "";	//controller类名
	private String controllerNameSpace = "";	//controller命名空间(不包含类名)
	private String propertiesPath = "";		//校验提示properties路径

	
	/**
	 * 生成文件 或 增加属性
	 * @param model new生成文件 add增加属性
	 * @param ctx ApplicationContext
	 * @param params 前台参数
	 */
	public void operatorFile(String model, ApplicationContext ctx, Map<String,String[]> params) {
		//前台选中的表
		String[] tableChked = params.get("tableChked");
		if(null == tableChked) {
			return;
		}

		//默认生成新文件模式
		if(VerifyUtils.isEmpty(model)) {
			model = "new";
		}

		//校验提示properties路径
		if(null != params.get("validationMessagesPath") && params.get("validationMessagesPath").length > 0) {
			this.propertiesPath = params.get("validationMessagesPath")[0];
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
			this.voClassName = tableClassName + "VO";
			this.serviceClassName = tableClassName + "Service";
			this.controllerClassName = tableClassName + "Controller";
			this.mapperName = tableJavaName + "Mapper";

			//设置命名空间
			if(isNeedCreate(params, "boNameSpace")) {
				this.boNameSpace = params.get("boNameSpace")[0];
			}
			if(isNeedCreate(params, "voNameSpace")) {
				this.voNameSpace = params.get("voNameSpace")[0];
			}
			if(isNeedCreate(params, "daoNameSpace")) {
				this.daoNameSpace = params.get("daoNameSpace")[0];
			}
			if(isNeedCreate(params, "serviceNameSpace")) {
				this.serviceNameSpace = params.get("serviceNameSpace")[0];
			}
			if(isNeedCreate(params, "controllerNameSpace")) {
				this.controllerNameSpace = params.get("controllerNameSpace")[0];
			}

			// 修改文件 获取要添加的列
			if(model.indexOf("add") != -1) {
				try {
					Class bo = Class.forName(boNameSpace + "." + boClassName);

					// 数据库列跟bo中的变量比对 获取要添加的列
					List<Map<String,String>> columnInfoList = (List<Map<String,String>>)this.column.get("column");
					for(int j=0; j<columnInfoList.size(); j++) {
						Map<String,String> columnInfo = columnInfoList.get(j);
						boolean isExit = false;
						for(int i=0; i<bo.getDeclaredFields().length; i++) {
							Field field = bo.getDeclaredFields()[i];
							if(columnInfo.get("javaName").equals(field.getName())) {
								isExit = true;
								break;
							}
						}
						if(!isExit) {
							Map<String,Object> addColumn = new HashMap<>();
							addColumn.put("columnIdx", j);	//添加列的序列
							addColumn.put("column", columnInfo);
							this.addColumns.add(addColumn);
						}
					}
				} catch (ClassNotFoundException e) {
					throw new ThrowException("读取实体错误，请重启后再试");
				}
			}

			//生成 或 修改文件
			if(isNeedCreate(params, "mapperPath")) {
				String filePath = params.get("mapperPath")[0] + File.separator + mapperName + ".xml";
				if(model.indexOf("new") != -1) {
					newMapperXML(filePath);
				} else {
					addMapperXML(filePath);
				}
			}
			if(isNeedCreate(params, "boPath")) {
				String filePath = params.get("boPath")[0] + File.separator + boClassName + ".java";
				if(model.indexOf("new") != -1) {
					newBo(filePath);
				} else {
					addBo(filePath);
				}
			}
			if(isNeedCreate(params, "voPath")) {
				if(model.indexOf("new") != -1) {
					String filePath = params.get("voPath")[0] + File.separator + voClassName + ".java";
					newVo(filePath);
				}
			}
			if(isNeedCreate(params, "daoPath")) {
				if(model.indexOf("new") != -1) {
					String filePath = params.get("daoPath")[0] + File.separator + daoClassName + ".java";
					newDao(filePath);
				}
			}
			if(isNeedCreate(params, "servicePath")) {
				String filePath = params.get("servicePath")[0] + File.separator + serviceClassName + ".java";
				if(model.indexOf("new") != -1) {
					newService(filePath);
				} else {
					addService(filePath);
				}
			}
			if(isNeedCreate(params, "controllerPath")) {
				String filePath = params.get("controllerPath")[0] + File.separator + controllerClassName + ".java";
				if(model.indexOf("new") != -1) {
					newController(filePath);
				} else {
					addController(filePath);
				}
			}
		}
	}
	
	/**
	 * 创建mapperXML
	 * @param path 生成路径
	 */
	private void newMapperXML(String path) {
		List<Map<String,String>> tableColumnList = columnList();

		StringBuffer resultBOMap = new StringBuffer();			//BO映射关系
		StringBuffer resultVOMap = new StringBuffer();			//VO映射关系
		StringBuffer insertDbNames = new StringBuffer();		//插入数据库列","分割
		StringBuffer insertJavaNames = new StringBuffer();		//插入实体属性列#{name}
		StringBuffer insertBatchJavaNames = new StringBuffer();	//批量插如实体属性#{item.name}
		StringBuffer updateNames = new StringBuffer();			//更新列 数据库名=#{item.name}
		StringBuffer updatBeatcheNames = new StringBuffer();	//批量更新列 数据库名=#{item.name}
		StringBuffer selectByOr = new StringBuffer();			//or查询条件
		StringBuffer selectByAnd = new StringBuffer();			//and查询条件

		resultBOMap.append("\t<resultMap type=\"").append(boNameSpace + "." + boClassName).append("\" ")
				   .append("id=\"").append(toJavaName(boClassName)).append("\">\n");
		
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
				resultBOMap.append("\t\t<id column=\"").append(dbName).append("\" property=\"").append(javaName).append("\" />\n");
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
						  .append("\t\t\t\tOR ").append(dbName).append("=#{" + javaName + "}\n")
						  .append("\t\t\t</if>\n");
				//selectByAnd
				selectByAnd.append("\t\t\t<if test=\"").append(javaName).append(" != null\">\n")
						   .append("\t\t\t\tAND ").append(dbName).append("=#{" + javaName + "}\n")
						   .append("\t\t\t</if>\n");

				resultBOMap.append("\t\t<result column=\"").append(dbName).append("\" property=\"").append(javaName).append("\" />\n");
			}
		}

		resultBOMap.append("\t</resultMap>");

		resultVOMap.append("\t<resultMap type=\"").append(voNameSpace + "." + voClassName).append("\" ")
				   .append("id=\"").append(toJavaName(voClassName)).append("\" ")
				   .append("extends=\"").append(toJavaName(boClassName)).append("\">\n")
				   .append("\t</resultMap>");
		
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
			.append(resultBOMap).append("\n")
			.append(resultVOMap).append("\n\n")
			
			//插入
			.append("\t<insert id=\"insert\" parameterType=\"").append(voNameSpace + "." + voClassName).append("\" useGeneratedKeys=\"true\" keyProperty=\"").append(columnDbPrimary()).append("\">\n")
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
			.append("\t<update id=\"update\" parameterType=\"").append(voNameSpace + "." + voClassName).append("\">\n")
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
			.append("\t<select id=\"findById\" parameterType=\"").append(toJavaName(columnJavaPrimaryType())).append("\" resultMap=\"").append(toJavaName(voClassName)).append("\">\n")
			.append("\t\tSELECT\n")
			.append("\t\t").append(insertDbNames).append("\n")
			.append("\t\tFROM ").append(tableName).append("\n")
			.append("\t\tWHERE ").append(columnDbPrimary()).append("=#{").append(columnJavaPrimary()).append("}\n")
			.append("\t</select>\n\n")
			
			//or条件查找
			.append("\t<select id=\"queryByOr\" parameterType=\"").append(voNameSpace + "." + voClassName).append("\" resultMap=\"").append(toJavaName(voClassName)).append("\">\n")
			.append("\t\tSELECT\n")
			.append("\t\t").append(insertDbNames).append("\n")
			.append("\t\tFROM ").append(tableName).append("\n")
			.append("\t\t<where>\n")
			.append(selectByOr)
			.append("\t\t</where>\n")
			.append("\t</select>\n\n")
			
			//and条件查找
			.append("\t<select id=\"queryByAnd\" parameterType=\"").append(voNameSpace + "." + voClassName).append("\" resultMap=\"").append(toJavaName(voClassName)).append("\">\n")
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
	private void newBo(String path) {
		StringBuffer attribute = new StringBuffer();
		StringBuffer getAndSet = new StringBuffer();
		StringBuffer importPack = new StringBuffer();

		List<Map<String,String>> tableColumnList = columnList();

		//遍历当前表的列
		for(Map<String,String> columnMap : tableColumnList) {
			//导入包
			this.getBoImport(columnMap, importPack);

			//属性
			this.getBoAttribute(columnMap, attribute);

			//get、set方法
			this.getBoGetAndSet(columnMap, getAndSet);
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
				.append("import com.fastjavaframework.base.BaseBean;\n\n")
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
	 * 获取bo导入包
	 * @param columnMap
	 * @param importPack
	 */
	private void getBoImport(Map<String,String> columnMap, StringBuffer importPack) {
		String type = columnType(columnMap);		//当前列数据类型
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
	}

	/**
	 * 获取bo 成员变量
	 * @param columnMap
	 * @param attribute
	 */
	private void getBoAttribute(Map<String,String> columnMap, StringBuffer attribute) {
		String name = columnJavaName(columnMap);	//当前列驼峰格式名
		String type = columnType(columnMap);		//当前列数据类型
		String remarks = columnRemarks(columnMap);	//当前列注释
		Map<String, String> annotation = columnAnnotation(tableName, columnMap);	//当前列注解

		String annotations = annotation.get("annotations");

		if(VerifyUtils.isNotEmpty(remarks)) {
			attribute.append("\t").append(remarks).append("\n");
		}

		attribute.append(null == annotations ? "" : annotations)
				.append("\tprivate ").append(type).append(" ").append(name).append(";\n\n");
	}

	/**
	 * 获取bo get set
	 * @param columnMap
	 * @param getAndSet
	 */
	private void getBoGetAndSet(Map<String,String> columnMap, StringBuffer getAndSet) {
		String name = columnJavaName(columnMap);	//当前列驼峰格式名
		String type = columnType(columnMap);		//当前列数据类型

		getAndSet.append("\tpublic ").append(type).append(" get").append(toClassName(name)).append("() {\n")
				.append("\t\treturn ").append(name).append(";\n")
				.append("\t}\n\n")
				.append("\tpublic ").append(boClassName).append(" set").append(toClassName(name)).append("(").append(type).append(" ").append(name).append(") {\n")
				.append("\t\tthis.").append(name).append(" = ").append(name).append(";\n")
				.append("\t\treturn this;\n")
				.append("\t}\n\n");
	}

	/**
	 * 创建vo层
	 * @param path 生成路径
	 */
	private void newVo(String path) {
		StringBuffer code = new StringBuffer();
		code.append("package ").append(voNameSpace).append(";\n\n")
			.append("import ").append(boNameSpace).append(".").append(boClassName).append(";\n\n")
			.append("public class ").append(voClassName).append(" extends ").append(boClassName).append(" {\n")
			.append("\tprivate static final long serialVersionUID = 1L;\n\n")
			.append("}");

		//生成文件
		FileUtil.writeFile(code.toString(), new File(path));
	}

	/**
	 * 创建dao层
	 * @param path 生成路径
	 */
	private void newDao(String path) {
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
			.append("import com.fastjavaframework.base.BaseDao;\n")
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
	private void newService(String path) {
		StringBuffer code = new StringBuffer();

		List<Map<String,String>> tableColumnList = columnList();

		//遍历当前表的列
		StringBuffer upColumn = new StringBuffer();
		for(Map<String,String> columnMap : tableColumnList) {
			this.setUpColumn(columnMap, upColumn);
		}

		//修改时间
		String importDate = "";
		if(!"".equals(updateTimeColumn)) {
			upColumn.append("\n\t\t//修改时间\n")
					.append("\t\tdbBO.set").append(toClassName(updateTimeColumn)).append("(new Date());\n\n");

			importDate = "import java.util.Date;\n";
		}
		
		//表注释信息
		StringBuffer remarks = new StringBuffer();
		if(!VerifyUtils.isEmpty(tableRemarks)) {
			remarks.append("/*\n")
				   .append(" * ").append(tableRemarks).append("\n")
				   .append(" */\n");
		}
		
		code.append("package ").append(serviceNameSpace).append(";\n\n")
			.append("import java.util.ArrayList;\n")
			.append("import java.util.List;\n")
			.append("import com.fastjavaframework.exception.ThrowPrompt;\n")
			.append(importDate)
			.append("import org.springframework.stereotype.Service;\n")
			.append("import com.fastjavaframework.base.BaseService;\n")
			.append("import ").append(daoNameSpace).append(".").append(daoClassName).append(";\n")
			.append("import ").append(voNameSpace).append(".").append(voClassName).append(";\n\n")
			.append(remarks)
			.append("@Service\n")
			.append("public class ").append(serviceClassName).append(" extends BaseService<").append(daoClassName).append(",").append(voClassName).append("> {\n\n")
			
			//更新
			.append("\t/**\n")
			.append("\t * 更新\n")
			.append("\t */\n")
			.append("\tpublic int update(").append(voClassName).append(" vo) {\n")
			.append("\t\t//设置修改值\n")
			.append("\t\t").append(voClassName).append(" upVO = this.setUpdateVlaue(super.baseFind(vo.getId()), vo);\n\n")
			.append("\t\t//更新\n")
			.append("\t\treturn super.baseUpdate(upVO);\n")
			.append("\t}\n\n")

			//批量更新
			.append("\t/**\n")
			.append("\t * 批量更新\n")
			.append("\t */\n")
			.append("\tpublic int updateBatch(List<").append(voClassName).append("> voList) {\n")
			.append("\t\tList<").append(voClassName).append("> upList = new ArrayList<>();\n\n")
			.append("\t\t//设置修改值\n")
			.append("\t\tfor(").append(voClassName).append(" vo : voList) {\n")
			.append("\t\t\tupList.add(this.setUpdateVlaue(super.baseFind(vo.getId()), vo));\n")
			.append("\t\t}\n\n")
			.append("\t\t//更新\n")
			.append("\t\treturn super.baseUpdateBatch(voList);\n")
			.append("\t}\n\n")

			.append("\t/**\n")
			.append("\t * 设置修改的属性(不为null为修改)\n")
			.append("\t * @param dbVO 库中最新vo\n")
			.append("\t * @param upVO	修改的vo\n")
			.append("\t * @return 修改后的vo\n")
			.append("\t */\n")
			.append("\tprivate ").append(voClassName).append(" setUpdateVlaue(").append(voClassName).append(" dbVO, ").append(voClassName).append(" upVO) {\n")
			.append("\t\tif(null == dbVO) {\n")
			.append("\t\t\tthrow new ThrowPrompt(\"无\"+upVO.getId()+\"信息！\");\n")
			.append("\t\t}\n\n")
			.append(upColumn)
			.append("\t\treturn dbVO;\n")
			.append("\t}\n\n")
			
			.append("}");

		//生成文件
		FileUtil.writeFile(code.toString(), new File(path));
	}

	/**
	 * 设置update修改的属性
	 * @param columnMap
	 * @param upColumn
     * @return
     */
	private void setUpColumn(Map<String,String> columnMap, StringBuffer upColumn) {
		String name = toClassName(columnJavaName(columnMap));	//当前列明
		String remarks = columnRemarks(columnMap);	//当前列注释
		String type = columnType(columnMap);	//当前列数据类型

		//不设置修改时间
		if(name.toLowerCase().equals(updateTimeColumn.toLowerCase())) {
			return;
		}

		//添加备注
		if(!"".equals(remarks)) {
			upColumn.append("\t\t").append(remarks).append("\n");
		}


		//不能为null类型 不判断 直接设置值, 否则先判断是否为null
		if("long".equals(type) || "float".equals(type) || "double".equals(type)) {
			upColumn.append("\t\tdbVO.set").append(name).append("(upVO.get").append(name).append("());\n");
		} else {
			upColumn.append("\t\tif(null != upVO.get").append(name).append("()) {\n")
					.append("\t\t\tdbVO.set").append(name).append("(upVO.get").append(name).append("());\n")
					.append("\t\t}\n");
		}
	}

	/**
	 * 创建controller层
	 * @param path 生成路径
	 */
	private void newController(String path) {
		StringBuffer code = new StringBuffer();
		
		String importDate = "";
		
		//创建设置项
		StringBuffer createSet = new StringBuffer();
		//uuid
		createSet.append("String".equals(columnJavaPrimaryType())?"\t\tvo.set"+toClassName(columnJavaPrimary())+"(UUID.uuid());\n\n":"");
		//创建时间
		if(!"".equals(createTimeColumn)) {
			createSet.append("\t\t//创建时间\n")
					.append("\t\tvo.set").append(toClassName(createTimeColumn)).append("(new Date());\n\n");
			
			importDate = "import java.util.Date;\n";
		}
		
		//批量创建设置项
		StringBuffer createBatchSet = new StringBuffer();
		createBatchSet.append("\t\tif(voArry.length == 0) {\n")
					.append("\t\t\tthrow new ThrowPrompt(\"无创建内容！\");\n")
					.append("\t\t}\n\n")
					.append("\t\tList<").append(voClassName).append("> voList = Arrays.asList(voArry);\n\n");
		
		if(!"".equals(createTimeColumn) || "String".equals(columnJavaPrimaryType())) {
			createBatchSet.append("\t\tfor(").append(voClassName).append(" vo : voList) {\n");
			
			//uuid
			if ("String".equals(columnJavaPrimaryType())) {
				createBatchSet.append("String".equals(columnJavaPrimaryType()) ? "\t\t\tvo.set"
								+ toClassName(columnJavaPrimary()) + "(UUID.uuid());\n" : "");
			}
			
			//创建时间
			if(!"".equals(createTimeColumn)) {
				createBatchSet.append("\t\t\t//创建时间\n")
						.append("\t\t\tvo.set").append(toClassName(createTimeColumn)).append("(new Date());\n");
				
				if(importDate.indexOf("java.util.Date") == -1) {
					importDate = "import java.util.Date;\n";
				}
			}
			
			createBatchSet.append("\t\t}\n\n");
		}
		
		//批量修改设置
		StringBuffer updateBatchSet = new StringBuffer();
		updateBatchSet.append("\t\tif(voArry.length == 0) {\n")
					.append("\t\t\tthrow new ThrowPrompt(\"无修改内容！\");\n")
					.append("\t\t}\n\n")
					.append("\t\tList<").append(voClassName).append("> voList = Arrays.asList(voArry);\n\n");
		
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
				delTime.append("\t\tvo.set").append(toClassName(delTimeColumn)).append("(new Date());\t//删除时间\n\n");
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
					.append("\t\t").append(voClassName).append(" vo = this.service.find(id);\n")
					.append("\t\tvo.set").append(toClassName(columnJavaPrimary())).append("(id);\n")
					.append("\t\tvo.set").append(toClassName(delColumn)).append("(").append(setFlag).append(");\t//删除标记\n")
					.append(delTime)
					.append("\t\tthis.service.baseDeleteLogic(vo);\n")
					.append("\t\treturn success();\n")
					.append("\t}\n\n")
					
					.append("\t/**\n")
					.append("\t * 批量逻辑删除\n")
					.append("\t */\n")
					.append("\t@RequestMapping(value=\"/logic/deleteBatch\",method=RequestMethod.DELETE)\n")
					.append("\tpublic Result deleteLogicBatch(@RequestBody List<").append(columnJavaPrimaryType()).append("> idList) {\n")
					.append(delBatchSet)
					.append("\t\t//设置id、删除标记\n")
					.append("\t\tList<").append(voClassName).append("> boList = new ArrayList<>();\n")
					.append("\t\tfor(").append(columnJavaPrimaryType()).append(" id : idList) {\n")
					.append("\t\t\t").append(voClassName).append(" vo = this.service.find(id);\n")
					.append("\t\t\tvo.set").append(toClassName(columnJavaPrimary())).append("(id);\n")
					.append("\t\t\tvo.set").append(toClassName(delColumn)).append("(").append(setFlag).append(");\t//删除标记\n")
					.append("\t").append(delTime)
					.append("\t\t\tboList.add(vo);\n")
					.append("\t\t}\n\n")
					.append("\t\tthis.service.baseDeleteLogicBatch(voList);\n")
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
					  .append("\t\t\t\t\t\t@RequestParam(required = false) String orderBy, ")
					  .append("@RequestParam(required = false) String orderSort");

		//设置controller query方法参数
		int nIndex = 1;	//参数换行序列
		for(int i=0; i<tableColumnList.size(); i++) {
			Map<String,String> columnMap = tableColumnList.get(i);
			nIndex = this.setQueryParam(columnMap, nIndex, queryCondition, queryConditionSet);

			if(!"".equals(queryCondition.toString()) && "".equals(importRequestParam.toString())) {
				importRequestParam.append("import org.springframework.web.bind.annotation.RequestParam;\n");
			}
		}
		
		//表注释信息
		StringBuffer remarks = new StringBuffer();
		if(!VerifyUtils.isEmpty(tableRemarks)) {
			remarks.append("/*\n")
				   .append(" * ").append(tableRemarks).append("\n")
				   .append(" */\n");
		}
		
		code.append("package ").append(controllerNameSpace).append(";\n\n")
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
			.append("String".equals(columnJavaPrimaryType())?"import com.fastjavaframework.util.UUID;\n":"")
			.append("import com.fastjavaframework.page.Page;\n")
			.append("import com.fastjavaframework.base.BaseController;\n")
			.append("import com.fastjavaframework.exception.ThrowPrompt;\n")
			.append("import ").append(voNameSpace).append(".").append(voClassName).append(";\n")
			.append("import ").append(serviceNameSpace).append(".").append(serviceClassName).append(";\n\n")
			.append(remarks)
			.append("@RestController\n")
			.append("@RequestMapping(value=\"/").append(tableJavaName).append("\")\n")
			.append("public class ").append(controllerClassName).append(" extends BaseController<").append(serviceClassName).append("> {\n\n")
			
			//create
			.append("\t/**\n")
			.append("\t * 创建\n")
			.append("\t */\n")
			.append("\t@RequestMapping(method=RequestMethod.POST)\n")
			.append("\tpublic Object create(@RequestBody ").append(voClassName).append(" vo) {\n")
			.append(createSet)
			.append("\t\tthis.service.baseInsert(vo);\n")
			.append("\t\treturn success(vo.get").append(toClassName(columnJavaPrimary())).append("()").append(");\n")
			.append("\t}\n\n")
			
			//createBatch
			.append("\t/**\n")
			.append("\t * 批量创建\n")
			.append("\t */\n")
			.append("\t@RequestMapping(value=\"/batch\",method=RequestMethod.POST)\n")
			.append("\tpublic Object createBatch(@RequestBody ").append(voClassName).append("[] voArry) {\n")
			.append(createBatchSet)
			.append("\t\tthis.service.baseInsertBatch(voList);\n")
			.append("\t\treturn success();\n")
			.append("\t}\n\n")
			
			//update
			.append("\t/**\n")
			.append("\t * 更新\n")
			.append("\t */\n")
			.append("\t@RequestMapping(value=\"/{id}\",method=RequestMethod.PUT)\n")
			.append("\tpublic Object update(@PathVariable String id, @RequestBody ").append(voClassName).append(" vo) {\n")
			.append("\t\tvo.set").append(toClassName(columnJavaPrimary())).append("(id);\n")
			.append("\t\tthis.service.update(vo);\n")
			.append("\t\treturn success();\n")
			.append("\t}\n\n")
			
			//updateBatch
			.append("\t/**\n")
			.append("\t * 批量更新\n")
			.append("\t */\n")
			.append("\t@RequestMapping(value=\"/batch\",method=RequestMethod.PUT)\n")
			.append("\tpublic Object updateBatch(@RequestBody ").append(voClassName).append("[] voArry) {\n")
			.append(updateBatchSet)
			.append("\t\tthis.service.updateBatch(voList);\n")
			.append("\t\treturn success();\n")
			.append("\t}\n\n")
			
			//逻辑删除
			.append(delLogic)
			
			//物理删除
			.append("\t/**\n")
			.append("\t * 物理删除\n")
			.append("\t */\n")
			.append("\t@RequestMapping(value=\"/{id}\",method=RequestMethod.DELETE)\n")
			.append("\tpublic Object delete(@PathVariable ").append(columnJavaPrimaryType()).append(" id) {\n")
			.append("\t\tthis.service.baseDelete(id);\n")
			.append("\t\treturn success();\n")
			.append("\t}\n\n")
			
			//批量物理删除
			.append("\t/**\n")
			.append("\t * 批量物理删除\n")
			.append("\t */\n")
			.append("\t@RequestMapping(value=\"/batch\",method=RequestMethod.DELETE)\n")
			.append("\tpublic Object deleteBatch(@RequestBody List<").append(columnJavaPrimaryType()).append("> idList) {\n")
			.append(delBatchSet)
			.append("\t\tthis.service.baseDeleteBatch(idList);\n")
			.append("\t\treturn success();\n")
			.append("\t}\n\n")
			
			//findById
			.append("\t/**\n")
			.append("\t * id查询详情\n")
			.append("\t */\n")
			.append("\t@RequestMapping(value=\"/{id}\",method=RequestMethod.GET)\n")
			.append("\tpublic Object findById(@PathVariable ").append(columnJavaPrimaryType()).append(" id) {\n")
			.append("\t\treturn success(this.service.baseFind(id));\n")
			.append("\t}\n\n")
			
			//query
			.append("\t/**\n")
			.append("\t * 列表查询 and条件\n")
			.append("\t */\n")
			.append("\t@RequestMapping(method=RequestMethod.GET)\n")
			.append("\tpublic Object query(").append(queryCondition).append(") {\n")
			.append("\t\t").append(voClassName).append(" vo = new ").append(voClassName).append("();\n")
			.append(queryConditionSet)
			.append("\t\t// 排序\n")
			.append("\t\tif(null != orderBy) {\n")
			.append("\t\t\tvo.setOrderBy(orderBy);\n")
			.append("\t\t}\n")
			.append("\t\tif(null != orderSort) {\n")
			.append("\t\t\tvo.setOrderSort(orderSort);\n")
			.append("\t\t}\n\n")
			.append("\t\tif(null != pageSize && null != pageNum && pageSize != 0 && pageNum != 0) {	//分页查询\n")
			.append("\t\t\tPage page = new Page();\n")
			.append("\t\t\tpage.setPageSize(pageSize);\n")
			.append("\t\t\tpage.setPageNum(pageNum);\n")
			.append("\t\t\tvo.setPage(page);\n\n")
			.append("\t\t\treturn success(this.service.baseQueryPageByAnd(vo));\n")
			.append("\t\t} else {	//列表查询\n")
			.append("\t\t\treturn success(this.service.baseQueryByAnd(vo));\n")
			.append("\t\t}\n")
			.append("\t}\n\n")
			
			.append("}");

		//生成类文件
		FileUtil.writeFile(code.toString(), new File(path));
	}

	/**
	 * 设置controllerler query方法参数
	 * @param columnMap
	 * @param nIndex			参数序列 用户换行
	 * @param queryCondition	保存参数的字符串
	 * @param queryConditionSet 保存set参数的字符串
     * @return nIndex
     */
	private int setQueryParam(Map<String,String> columnMap,
							   int nIndex, StringBuffer queryCondition,
							   StringBuffer queryConditionSet) {

		String columName = columnJavaName(columnMap);	//当前列明
		String columRemarks = columnRemarks(columnMap);	//当前列注释
		String columType = columnType(columnMap);	//当前列数据类型

		//跳过主键
		if(columnJavaPrimary().toLowerCase().equals(columName.toLowerCase())) {
			return nIndex;
		}

		//不能为null类型 不判断 直接设置值, 否则先判断是否为null
		if(!"long".equals(columType) && !"float".equals(columType) && !"double".equals(columType) && !"Date".equals(columType)) {
			//参数逗号
			queryCondition.append(",");

			//参数换行
			if(nIndex%2 == 1) {	//2个换行
				queryCondition.append("\n\t\t\t\t\t\t");
			} else {
				queryCondition.append(" ");
			}
			nIndex++;

			queryCondition.append("@RequestParam(required = false) ").append(columType).append(" ").append(columName);


			//添加备注
			if(!"".equals(columRemarks)) {
				queryConditionSet.append("\t\t").append(columRemarks).append("\n");
			}

			//set参数
			queryConditionSet.append("\t\tif(null != ").append(columName).append(") {\n")
					.append("\t\t\tvo.set").append(toClassName(columName)).append("(").append(columName).append(");\n")
					.append("\t\t}\n");
		}

		return nIndex;
	}

	/**
	 * 要入插入节点的上个节点
	 * dom获取节点含有#text，用此方法过滤
	 * @param nodeList 节点列表
	 * @param idx	要插入节点的序列位置
     * @return
     */
	private Node getItemNodeByIdx(NodeList nodeList, int idx) {
		int realIdx = -1;
		Node lineFirstNode = null;
		for(int i=0; i<nodeList.getLength(); i++) {
			Node itemNode = nodeList.item(i);
			if("id".equals(itemNode.getNodeName()) || "result".equals(itemNode.getNodeName())) {
				realIdx++;

				if(realIdx == idx) {
					return lineFirstNode==null?nodeList.item(i):lineFirstNode;
				}
			} else {
				lineFirstNode = itemNode;
			}
		}

		return null;
	}

	/**
	 * 增加模式 修改xml
	 * @param path
     */
	private void addMapperXML(String path) {
		DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		// 获取文件
		try {
			File file = new File(path);

			// 文件不存在新建
			if(!file.exists()) {
				newMapperXML(path);
				return;
			}

			Document document = builder.parse(file);

			if(this.addColumns.size() == 0) {
				return;
			}

			for(Map<String, Object> column : this.addColumns) {
				int columnIdx = (int)column.get("columnIdx");	//增加列的位置

				Map<String, String> columnInfo = (Map<String, String>)column.get("column");	//增加列的信息

				String thisColumnDbName = this.columnDbName(columnInfo);
				String thisColumnJavaName = this.columnJavaName(columnInfo);

				NodeList itemNodeList = document.getChildNodes().item(1).getChildNodes();
				for(int i=0; i<itemNodeList.getLength(); i++) {
					Node itemNode = itemNodeList.item(i);

					// 解析各个几点
					// resultMap id是BO的节点
					if("resultMap".equals(itemNode.getNodeName()) && ((DeferredElementImpl) itemNode).getAttribute("id").endsWith("BO")) {

						// 过滤已添加项
						NodeList results = ((DeferredElementImpl) itemNode).getElementsByTagName("result");
						boolean isAdd = false;
						for(int j=0; j<results.getLength(); j++){
							if(thisColumnDbName.equals(results.item(j).getAttributes().getNamedItem("column").getNodeValue())) {
								isAdd = true;
								break;
							}
						}
						if(isAdd) {
							continue;
						}

						// 新节点
						Element newResultNode = document.createElement("result");
						newResultNode.setAttribute("column", thisColumnDbName);
						newResultNode.setAttribute("property", thisColumnJavaName);

						// 子节点个数(id+result)
						int realItemSum = ((DeferredElementImpl) itemNode).getElementsByTagName("id").getLength() +
								results.getLength();

						// 结尾插入
						if(columnIdx >= realItemSum) {
							itemNode.appendChild(document.createTextNode("\t"));
							itemNode.appendChild(newResultNode);
							itemNode.appendChild(document.createTextNode("\n"));
							itemNode.appendChild(document.createTextNode("\t"));
						} else {	//按顺序插入
							//要入插入节点的上个节点
							Node nowNode = this.getItemNodeByIdx(itemNode.getChildNodes(), columnIdx);
							itemNode.insertBefore(newResultNode, nowNode);
						}

					}
				}
			}


			//设置xml
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", new Integer(4));	// 缩进已4个空格为看为

			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("encoding", "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");	//自动缩进
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//mybatis.org//DTD Mapper 3.0//EN");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://mybatis.org/dtd/mybatis-3-mapper.dtd");

			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			String code = writer.toString();

			//用字符串模式设置xml
			// insert
			String regex = "<insert[\\s\\S]*?</insert>";
			Matcher insertMatcher = Pattern.compile(regex).matcher(code);
			while (insertMatcher.find()) {
				String insertCode = insertMatcher.group(0);

				// 列
				String newColumnStr = this.setSql(insertCode, "column");

				// value
				newColumnStr = this.setSql(newColumnStr, "value");
				code = code.replace(insertCode, newColumnStr);
			}

			// select
			regex = "<select[\\s\\S]*?</select>";
			Matcher selectMatcher = Pattern.compile(regex).matcher(code);
			while (selectMatcher.find()) {
				String selectCode = selectMatcher.group(0);

				// 列
				String newColumnStr = this.setSql(selectCode, "column");
				code = code.replace(selectCode, newColumnStr);
			}

			// update
			regex = "<update[\\s\\S]*?</update>";
			Matcher updateMatcher = Pattern.compile(regex).matcher(code);
			while (updateMatcher.find()) {
				String updateCode = updateMatcher.group(0);

				// 列
				String newColumnStr = this.setSql(updateCode, "column");
				code = code.replace(updateCode, newColumnStr);
			}

			FileUtil.writeFile(code, new File(path));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置sql
	 * @param domContent xml节点内容
	 * @param type	column设置列 value设置值
	 * @return
     */
	private String setSql(String domContent, String type) {
		String regex = "column".equals(type)?
				"((?i)INSERT\\s+(?i)"+this.tableName+"\\s*.*\\s*(?i)VALUES)|((?i)SELECT\\s*.*\\s*(?i)FROM)":
				"((?i)VALUES<|(?i)VALUES)[\\s\\S]*?(></insert>|</insert>)";
		Matcher columnMatcher = Pattern.compile(regex).matcher(domContent);

		if (columnMatcher.find()) {
			String columnStr = columnMatcher.group(0);
			if("column".equals(type)) {
				columnStr = columnStr
							.replaceAll("(?i)INSERT","")
							.replaceAll("(?i)VALUES","")
							.replaceAll("(?i)SELECT","")
							.replaceAll("(?i)FROM","")
							.replaceAll("(\\s|\r|\t|\n)", "")
							.replaceAll("\\(","")
							.replaceAll("\\)","");
			} else {
				columnStr = columnStr.replaceAll("(\\s|\r|\t|\n)", "")
							.replaceAll("<[\\s\\S]*?>","")
							.replaceAll("(?i)VALUES","")
							.replaceAll("</insert>","")
							.replaceAll("\\(","")
							.replaceAll("\\)","");
			}

			// 当前列或值集合
			List<String> columnStrs =  new ArrayList(Arrays.asList(columnStr.split(",")));
			// 最新列或值的集合
			String[] newColumnStrs = new String[columnStrs.size()+this.addColumns.size()];

			// 增加新列或值
			Iterator<Map<String, Object>> addColsIter = this.addColumns.iterator();
			while(addColsIter.hasNext()) {
				Map<String, Object> column = addColsIter.next();
				int columnIdx = (int) column.get("columnIdx");    //增加列的位置
				Map<String, String> columnInfo = (Map<String, String>) column.get("column");    //增加列的信息

				if(columnStrs.lastIndexOf(this.columnDbName(columnInfo)) != -1) {
					addColsIter.remove();
					continue;
				}

				if("value".equals(type)) {
					// 有些value以#{XXX.YYY}格式存在，以此格式更新
					regex = "(\\{|\\.).*\\}";
					Matcher valueMatcher = Pattern.compile(regex).matcher(columnStrs.get(0));
					if(valueMatcher.find()) {
						String key = valueMatcher.group(0).replaceAll("(\\{.*\\.|\\{)","").replace("}","");
						newColumnStrs[columnIdx] = columnStrs.get(0).replace(key, this.columnJavaName(columnInfo));
					}
				} else {
					newColumnStrs[columnIdx] = this.columnDbName(columnInfo);
				}
			}

			// 回填老列或值
			for(int i=newColumnStrs.length-1; i>=0; i--) {
				if(VerifyUtils.isEmpty(newColumnStrs[i])) {
					int columnStrsBefSize = columnStrs.size()-1;
					if(columnStrsBefSize >= 0) {
						newColumnStrs[i] = columnStrs.get(columnStrsBefSize);
						columnStrs.remove(columnStrsBefSize);
					}
				}
			}

			StringJoiner joiner = new StringJoiner(",");
			for (CharSequence cs: newColumnStrs) {
				if(null != cs) {
					joiner.add(cs);
				}
			}
			domContent = domContent.replace(columnStr, joiner.toString());
		}


		// 最新列的where条件或update值
		StringBuffer whereOrUpdateStr = new StringBuffer();
		for(Map<String, Object> column : this.addColumns) {
			Map<String, String> columnInfo = (Map<String, String>) column.get("column");    //增加列的信息

			whereOrUpdateStr.append("\t<if test=\"").append(this.columnJavaName(columnInfo)).append(" != null\">\n\t\t\t\t");
			if(domContent.startsWith("<update")) {
				if(domContent.indexOf("<foreach") != -1) {
					whereOrUpdateStr.append("\t");
				}
				whereOrUpdateStr.append(this.columnDbName(columnInfo))
						.append("=#{").append(this.columnJavaName(columnInfo)).append("},")
						.append("\n\t\t\t");
				if(domContent.indexOf("<foreach") != -1) {
					whereOrUpdateStr.append("\t");
				}
				whereOrUpdateStr.append("</if>\n\t\t");
				if(domContent.indexOf("<foreach") != -1) {
					whereOrUpdateStr.append("\t");
				}
			} else if(domContent.startsWith("<select")) {
				String operatorStr = "OR ";
				if(domContent.indexOf("AND ") != -1) {
					operatorStr = "AND ";
				}
				whereOrUpdateStr.append(operatorStr).append(this.columnDbName(columnInfo))
						.append("=#{").append(this.columnJavaName(columnInfo)).append("}")
						.append("\n\t\t\t</if>\n\t\t");
			}
		}

		if(domContent.startsWith("<update")) {
			int trimIdx = domContent.lastIndexOf("</trim>");
			if(trimIdx > -1) {
				domContent = new StringBuffer(domContent).insert(trimIdx, whereOrUpdateStr).toString();
			}
		} else if(domContent.startsWith("<select")
				&& (domContent.indexOf("id=\"findById\"") == -1 || domContent.indexOf("parameterType=\"string\"") == -1)) {
			int trimIdx = domContent.lastIndexOf("</where>");
			if(trimIdx != -1) {
				domContent = new StringBuffer(domContent).insert(trimIdx, whereOrUpdateStr).toString();
			}
		}

		return domContent;
	}

	/**
	 * 增加模式 修改bo
	 * @param path
     */
	private void addBo(String path) {
		//源代码
		String code = FileUtil.readFile(path, "UTF-8");

		//文件不存在则新建
		if(VerifyUtils.isEmpty(code)) {
			newBo(path);
			return;
		}

		if(this.addColumns.size() == 0) {
			return;
		}

		StringBuffer attribute = new StringBuffer();
		StringBuffer getAndSet = new StringBuffer();
		StringBuffer importPack = new StringBuffer();

		for(Map<String, Object> column : this.addColumns) {
			Map<String, String> columnInfo = (Map<String, String>)column.get("column");	//增加列的信息

			//导入包
			this.getBoImport(columnInfo, importPack);

			//属性
			this.getBoAttribute(columnInfo, attribute);

			//get、set方法
			this.getBoGetAndSet(columnInfo, getAndSet);
		}

		int classHeadIdx = code.lastIndexOf("public class");
		String variableStr = code.substring(code.lastIndexOf("private ")).split("\n")[0];
		int variableIdx = code.lastIndexOf(variableStr) + variableStr.length();
		int getSetIdx = code.lastIndexOf("}");

		StringBuffer bufferCode = new StringBuffer(code);

		//get、set方法
		bufferCode.insert(getSetIdx - 1, getAndSet);

		//属性
		bufferCode.insert(variableIdx, "\n\n" + attribute);

		//导入包
		if(bufferCode.indexOf(importPack.toString()) == -1) {
			importPack.append("\n");
			bufferCode.insert(classHeadIdx, importPack);
		}

		//生成类文件
		FileUtil.writeFile(bufferCode.toString().replace("\n\n\n\n","\n\n").replace("\n\n\n}","\n\n}"), new File(path));
	}

	/**
	 * 增加模式 修改service
	 * @param path
     */
	private void addService(String path) {
		//源代码
		String code = FileUtil.readFile(path, "UTF-8");

		//文件不存在则新建
		if(VerifyUtils.isEmpty(code)) {
			newService(path);
			return;
		}

		if(this.addColumns.size() == 0) {
			return;
		}

		//设置update修改的属性
		StringBuffer upColumn = new StringBuffer();
		for(Map<String, Object> column : this.addColumns) {
			Map<String, String> columnInfo = (Map<String, String>)column.get("column");	//增加列的信息
			this.setUpColumn(columnInfo, upColumn);
		}

		//修改时间
		if(!"".equals(updateTimeColumn)) {
			upColumn.append("\n\t\t//修改时间\n")
					.append("\t\tdbBO.set").append(toClassName(updateTimeColumn)).append("(new Date());\n\n");

			// 设置date import
			if(code.indexOf("import java.util.Date;") == -1) {
				String importDate = "import java.util.Date;\n";

				int importIdx = code.indexOf("import ");
				code = new StringBuffer(code).insert(importIdx, importDate).toString();
			}
		}

		String regex = "(private ).*( setUpdateVlaue\\()";
		Matcher functionMatcher = Pattern.compile(regex).matcher(code);

		if (functionMatcher.find()) {
			String fnctionStr = functionMatcher.group(0);

			int functionIdx = code.indexOf(fnctionStr);
			int returnIdx = code.indexOf("return ", functionIdx);

			if(upColumn.toString().startsWith("\t\t")) {
				upColumn.replace(0, 2, "");
			}

			code = new StringBuffer(code).insert(returnIdx, upColumn + "\t\t").toString();
		} else {
			return;
		}

		//生成类文件
		FileUtil.writeFile(code, new File(path));
	}

	/**
	 * 增加模式 修改controller
	 * @param path
     */
	private void addController(String path) {
		//源代码
		String code = FileUtil.readFile(path, "UTF-8");

		//文件不存在则新建
		if(VerifyUtils.isEmpty(code)) {
			newController(path);
			return;
		}


		int paramIdx = 0;	//插入参数的位置
		int commaSum = 0;	//入参分隔符“,”的个数，用户计算新增参数换行

		//查找入参位置，分隔符个数
		String regex = "(public ).*( query\\()";
		Matcher functionMatcher = Pattern.compile(regex).matcher(code);
		if (functionMatcher.find()) {
			String fnctionStr = functionMatcher.group(0);
			int functionIdx = code.indexOf(fnctionStr);
			int funBodyBegIdx = code.indexOf("{", functionIdx);
			paramIdx = code.substring(0, funBodyBegIdx).lastIndexOf(")");

			commaSum = fnctionStr.split(",").length;
		} else {
			return;
		}

		//设置新增列的入参、setValue、import
		StringBuffer queryCondition = new StringBuffer();
		StringBuffer queryConditionSet = new StringBuffer();
		StringBuffer importRequestParam = new StringBuffer();
		String importStr = "import org.springframework.web.bind.annotation.RequestParam;\n";

		if(this.addColumns.size() == 0) {
			return;
		}

		for(Map<String, Object> column : this.addColumns) {
			Map<String, String> columnInfo = (Map<String, String>)column.get("column");	//增加列的信息
			commaSum = this.setQueryParam(columnInfo, commaSum, queryCondition, queryConditionSet);

			if(!"".equals(queryCondition.toString()) && "".equals(importRequestParam.toString()) && code.indexOf(importStr) == -1) {
				importRequestParam.append(importStr);
			}
		}

		//增加入参
		if(paramIdx != 0) {
			code = new StringBuffer(code).insert(paramIdx, queryCondition).toString();
		}

		//增加setValue
		int voIdx = code.indexOf(" vo", paramIdx);
		int setIdx = code.indexOf("\n\n", voIdx);
		code = new StringBuffer(code).insert(setIdx, "\n" + queryConditionSet.substring(0, queryConditionSet.length()-1)).toString();

		//增加import
		if(!"".equals(importRequestParam.toString())) {
			int importIdx = code.indexOf("import ");
			code = new StringBuffer(code).insert(importIdx, importRequestParam.toString()).toString();
		}

		//生成类文件
		FileUtil.writeFile(code, new File(path));
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
		if ("Integer".equals(type) || "long".equals(type)
				|| "float".equals(type) || "double".equals(type)
				|| "Decimal".equals(type)) {

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
		} else if("String".equals(type)) {	//@Size
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
		return null == map.get("remarks") || "".equals(map.get("remarks").toString())?"":"// " + map.get("remarks");
	}
	
	/**
	 * 前台是否返回对应的路径，不返回则不生成
	 * @param params    前台返回参数
	 * @param paramName 参数名
	 * @return 是否存在
	 */
	private boolean isNeedCreate(Map<String,String[]> params, String paramName) {
		if(null != params.get(paramName) && params.get(paramName).length > 0 && !"".equals(params.get(paramName)[0])) {
			return true;
		}
		
		return false;
	}
	
}
