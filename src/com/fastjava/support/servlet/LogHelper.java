package com.fastjava.support.servlet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fastjava.support.util.XmlDom;
import com.fastjava.util.FileUtil;
import com.fastjava.util.VerifyUtils;

/**
 * 日志管理
 */
public class LogHelper {

	//读取spring配置文件路径
	public Map<String,String> readPath(String projectPath) {
		Map<String,String> replaceMap = new HashMap<>();
		
		if(VerifyUtils.isEmpty(projectPath)) {
			return replaceMap;
		}
		
		//遍历项目子目录
		List<String> paths = FileUtil.iterator(projectPath, "file", "path", true);
		for(String path : paths) {
			String rePath = "\"" + path.replaceAll("\\\\", "\\\\\\\\") + "\"";
			
			if(path.indexOf("log4j.xml") != -1 && path.indexOf("class") == -1) {
				replaceMap.put("log4jPath", rePath);
			} else if(path.indexOf("dataSource.xml") != -1 && path.indexOf("class") == -1) {
				replaceMap.put("dataSourcePath", rePath);
			}
		}
		return replaceMap;
	}
	
	/**
	 * 检验sql打印-log4j.xml是否启动
	 * @param logjPath log4j配置文件绝对路径
	 * @return sql打印是否启用
	 */
	public Map<String,String> sqlInfo(String logjPath) {
		Map<String,String> returnMap = new HashMap<>();

		//beans节点
		NodeList beanList = new XmlDom().readRootNode(logjPath, "log4j:configuration");
		if(null == beanList) {
			return returnMap;
		}

		//错误信息类节点
		for(int i = 0; i < beanList.getLength(); i++) {
			Node bean = beanList.item(i);
			NamedNodeMap beanAttrs = bean.getAttributes();
			
			if(null != beanAttrs 
					&& null != beanAttrs.getNamedItem("name") 
					&& "jdbc.sqltiming".equals(beanAttrs.getNamedItem("name").getNodeValue())) {
				returnMap.put("sqlSwitch", "true");
				break;
			}
		}
		return returnMap;
	}
	
	/**
	 * 检验sql打印-jdbcUrl是否配置
	 * @param dataSourcePath dataSource配置文件绝对路径
	 * @return jdbcUrl是否配置
	 */
	public Map<String,String> dataSourceInfo(String dataSourcePath) {
		Map<String,String> returnMap = new HashMap<>();

		//beans节点
		NodeList beanList = new XmlDom().readRootNode(dataSourcePath, "beans");
		if(null == beanList) {
			return returnMap;
		}

		//bean节点
		for(int i = 0; i < beanList.getLength(); i++) {
			Node bean = beanList.item(i);
			NamedNodeMap beanAttrs = bean.getAttributes();
			
			//dataSource节点
			if(null != beanAttrs 
					&& null != beanAttrs.getNamedItem("id") 
					&& "dataSource".equals(beanAttrs.getNamedItem("id").getNodeValue())) {

				for(int j=0; j<bean.getChildNodes().getLength(); j++) {
					Node property = bean.getChildNodes().item(j);
					NamedNodeMap propertyAttrs = property.getAttributes();

					//jdbcUrl节点
					if(null != propertyAttrs 
							&& null != propertyAttrs.getNamedItem("name") 
							&& "url".equals(propertyAttrs.getNamedItem("name").getNodeValue())) {
						if(propertyAttrs.getNamedItem("value").getNodeValue().indexOf("log4jdbc:") != -1) {
							returnMap.put("dataSourceSwitch", "true");
							break;
						}
					}
				}
			}
		}
		return returnMap;
	}
	
}
