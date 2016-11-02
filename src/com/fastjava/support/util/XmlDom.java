package com.fastjava.support.util;

import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.fastjava.exception.ThrowException;
import com.fastjava.util.VerifyUtils;

/**
 * jdom操作
 */
public class XmlDom {
	
	/**
	 * 读取根节点下的子节点
	 * @param path	xml绝对路径
	 * @param rootNode 根节点名称
	 * @return 子节点
	 */
	public NodeList readRootNode(String path, String rootNode) {
		if(VerifyUtils.isEmpty(path)) {
			return null;
		} else {
			path = path.replaceAll("\"", "");
		}
		
		//xml dom
        Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dbd = dbf.newDocumentBuilder();
	        dbd.setEntityResolver(new DTDEntityResolver());
			doc = dbd.parse(new FileInputStream(path));
		} catch (Exception e) {
			throw new ThrowException("读取配置文件错误：" + e.getMessage());
		}
		
		//根节点
		if(doc.getElementsByTagName(rootNode).getLength() == 0) {
			return null;
		}
		return doc.getElementsByTagName(rootNode).item(0).getChildNodes();
	}
	
}
