package com.fastjavaframework.support.servlet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fastjavaframework.support.util.XmlDom;
import com.fastjavaframework.util.FileUtil;
import com.fastjavaframework.util.VerifyUtils;

/**
 * 模块管理
 */
public class ModuleHelper {

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
			
			if(path.indexOf("applicationContext.xml") != -1 && path.indexOf("class") == -1) {
				replaceMap.put("springPath", rePath);
			} else if(path.indexOf("web.xml") != -1) {
				replaceMap.put("webXmlPath", rePath);
			}
		}
		return replaceMap;
	}
	
	/**
	 * 校验是否配置错误信息类
	 * @param springPath spring配置文件绝对路径
	 * @return 错误信息类是否启用
	 */
	public Map<String,String> errInfo(String springPath) {
		Map<String,String> returnMap = new HashMap<>();

		//beans节点
		
		NodeList beanList = new XmlDom().readRootNode(springPath, "beans");
		if(null == beanList) {
			return returnMap;
		}

		//错误信息类节点
		for(int i = 0; i < beanList.getLength(); i++) {
			Node bean = beanList.item(i);
			NamedNodeMap beanAttrs = bean.getAttributes();
			
			if(null != beanAttrs 
					&& null != beanAttrs.getNamedItem("class") 
					&& "com.fastjava.Exception.ExceptionHandler".equals(beanAttrs.getNamedItem("class").getNodeValue())) {
				returnMap.put("errSwitch", "true");
				break;
			}
		}
		return returnMap;
	}
	
	/**
	 * 校验是否配置druid
	 * @param webXmlPath web.xml绝对路径
	 * @return druid是否启用
	 */
	public Map<String,String> druidInfo(String webXmlPath) {
		Map<String,String> returnMap = new HashMap<>();
		
		//web-app节点
		NodeList beanList = new XmlDom().readRootNode(webXmlPath, "web-app");
		if(null == beanList) {
			return returnMap;
		}

		//查找druid拦截器
		for(int i = 0; i < beanList.getLength(); i++) {
			Node bean = beanList.item(i);
			for(int j=0; j<bean.getChildNodes().getLength(); j++) {
				if("servlet-class".equals(bean.getChildNodes().item(j).getNodeName()) 
						&& "com.alibaba.druid.support.http.StatViewServlet".equals(bean.getChildNodes().item(j).getFirstChild().getNodeValue())) {
					returnMap.put("druidSwitch", "true");
					break;
				}
			}
		}
		return returnMap;
	}
	
}
