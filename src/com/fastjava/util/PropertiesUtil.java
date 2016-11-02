package com.fastjava.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.fastjava.exception.ThrowException;

/**
 * 公共工具类
 */
public class PropertiesUtil {

	/**
	 * 读取路径为filePath的properties文件属性，返回map
	 * @param filePath
	 * @return valueMap
	 */
	public Map<String, String> getValueMap(String filePath){
		// 创建返回 MAP
		HashMap<String, String> valueMap = new HashMap<String, String>();
		// 读取文件
		Properties properties = null;
		try {
			properties = this.getPropertyFile(filePath);
		} catch (Exception e) {
			throw new ThrowException("读取路径："+filePath+"下的PropertyFile属性出错：" + e.getMessage());
		}
		// 读取参数+回填参数
		if (properties != null && !"".equals(properties)) {
			 // 返回Properties中包含的key-value的Set视图  
	        Set<Entry<Object, Object>> set = properties.entrySet();  
	        // 返回在此Set中的元素上进行迭代的迭代器  
	        Iterator<Map.Entry<Object, Object>> it = set.iterator();  
	        String key = null, value = null;  
	        // 循环取出key-value  
	        while (it.hasNext()) {  
	            Entry<Object, Object> entry = it.next();  
	            key = String.valueOf(entry.getKey());  
	            value = String.valueOf(entry.getValue());  
	            key = key == null ? key : key.trim();  
	            value = value == null ? value : value.trim();  
	            valueMap.put(key, value);  
	        }  
		}
		return valueMap;
	}
	
	/**
	 * 传入相对路径+文件名获取到绝对路径
	 * @param 	filePath项目根目录开始的文件路径
	 * @return 	absolutePate绝对路径
	 */
	public String getAbsolutePath(String filePath){
		String rString = null;
		try {
			rString = getClass().getClassLoader().getResource("/").getPath() + filePath;
		} catch (Exception e) {
			throw new ThrowException("读取路径："+filePath+"下的PropertyFile属性出错：" + e.getMessage());
		}
		return rString;
	} 

	/**
	 * 描述： 	读取 FilePath 路径下的 Properties 文件
	 * @param 	FilePath
	 * @return 	Properties
	 * @author 	Zrain @ 2014-08-13
	 */
	public Properties getPropertyFile(String filePath) throws Exception {
		Properties properties = new Properties();
		try {
			InputStream inputStream = new FileInputStream(filePath);
			properties.load(inputStream);
			inputStream.close();
		} catch (Exception e) {
			throw new ThrowException("读取路径："+filePath+"下的PropertyFile属性出错：" + e.getMessage());
		}
		return properties;
	}

}
