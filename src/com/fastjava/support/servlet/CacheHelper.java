package com.fastjava.support.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fastjava.support.util.Db;
import com.fastjava.support.util.SqlMapperBean;
import com.fastjava.support.util.XmlDom;
import com.fastjava.util.FileUtil;
import com.fastjava.util.VerifyUtils;

/**
 * 缓存管理
 */
public class CacheHelper {

	//读取mapper路径
	public Map<String,String> readPath(String projectPath) {
		Map<String,String> replaceMap = new HashMap<>();
		
		if(VerifyUtils.isEmpty(projectPath)) {
			return replaceMap;
		}
		
		//遍历项目子目录
		List<String> paths = FileUtil.iterator(projectPath, "", "path", true);
		for(int i=0; i<paths.size(); i++) {
			if(paths.get(i).indexOf("Mapper.xml") != -1 && paths.get(i).indexOf("class") == -1) {
				String rePath = "\"" + paths.get(i-1).replaceAll("\\\\", "\\\\\\\\") + "\"";
				replaceMap.put("mapperPath", rePath);
				break;
			}
		}
		return replaceMap;
	}
	
	/**
	 * 读取实体缓存信息
	 * @param mapperPath mapper路径
	 * @param WebApplicationContext
	 * @return
	 */
	public Map<String,String> cacheInfo(String mapperPath, WebApplicationContext ctx) {
		Map<String,String> replaceMap = new HashMap<>();
		
		List<String> paths = FileUtil.iterator(mapperPath, "file", "path", true);
		if(paths.size() == 0) {
			return replaceMap;
		}
		
		//数据库表名
		Db db = new Db();
		Set<String> tables = db.getTablesName(db.getDb(ctx));
		
		List<SqlMapperBean> cacheList = new ArrayList<>();
		List<SqlMapperBean> refList = new ArrayList<>();
		List<SqlMapperBean> noList = new ArrayList<>();
		for(String path : paths) {
			String content = FileUtil.readFile(path);
			NodeList mapperList = new XmlDom().readRootNode(path, "mapper");
			if(null == mapperList || mapperList.getLength() == 0) {
				continue;
			}

			SqlMapperBean sqlMapperBean = new SqlMapperBean();
			String[] pathAttr = path.split("\\\\");
			sqlMapperBean.setMapperName(pathAttr[pathAttr.length-1]);
			sqlMapperBean.setNameSpace(mapperList.item(0).getParentNode().getAttributes().getNamedItem("namespace").getNodeValue());
			
			String activeTableName = "";
			String refNameSpace = "";
			Set<String> refTableName = new HashSet<>();
			for(int i = 0; i < mapperList.getLength(); i++) {
				Node mapper = mapperList.item(i);
				if(null == mapper) {
					continue;
				}
				
				if("insert".equals(mapper.getNodeName()) || "update".equals(mapper.getNodeName()) || "delete".equals(mapper.getNodeName()) || "select".equals(mapper.getNodeName())) {
					String sql = mapper.getTextContent().toUpperCase();
					for(String tableName : tables) {
						if(sql.indexOf(tableName) != -1) {
							refTableName.add(tableName);
						}
						
						if("".equals(activeTableName) && sqlMapperBean.getNameSpace().toUpperCase().indexOf(tableName.replace("_", "")) != -1) {
							activeTableName = tableName;
						}
					}
				}
				
				NamedNodeMap mapperAttr = mapper.getAttributes();
				if(content.indexOf("<cache-ref") != -1 && "".equals(refNameSpace) && null != mapperAttr && null != mapperAttr.getNamedItem("namespace")) {
					refNameSpace = mapperAttr.getNamedItem("namespace").getNodeValue();
				}
			}
			sqlMapperBean.setTableName(activeTableName);
			refTableName.remove(activeTableName);
			sqlMapperBean.setRefTableName(refTableName);
			
			 if(content.indexOf("<cache-ref") != -1) {
				sqlMapperBean.setRefNameSpace(refNameSpace);
				refList.add(sqlMapperBean);
			} else if(content.indexOf("<cache") != -1) {
				cacheList.add(sqlMapperBean);
			} else {
				noList.add(sqlMapperBean);
			}
		}

		StringBuffer successRelation = new StringBuffer();
		StringBuffer failRelation = new StringBuffer();
		for(SqlMapperBean cache : cacheList) {
			String success = "";
			for(String refName : cache.getRefTableName()) {
				
				//未添加ref的xml
				for(SqlMapperBean no : noList) {
					if(refName.equals(no.getTableName())) {
						failRelation.append(no.getMapperName())
									.append(":")
									.append("应添加<cache type=\"org.mybatis.caches.ehcache.LoggingEhcache\"/>;");
						break;
					}
				}
				
				for(SqlMapperBean ref : refList) {
					if(refName.equals(ref.getTableName())) {
						if(cache.getNameSpace().equals(ref.getRefNameSpace())) {
							//成功
							if(!"".equals(success)) {
								success += ",";
							}
							success += ref.getMapperName();
						} else {
							//关联不对
							failRelation.append(ref.getMapperName())
										.append(":")
										.append("<cache-ref> namespace应改为 “").append(cache.getNameSpace()).append("”;");
						}
						break;
					}
				}
			}
			
			if(!"".equals(success)) {
				successRelation.append(cache.getMapperName()).append(":").append(success).append(";");
			}
		}

		//ref未与cache关联 错误
		//ref与cache关联 cache未包含表 警告
		//关联表 关联主表 ref不同
		//关联表 关联从从表 ref不同
		for(SqlMapperBean ref : refList) {
			boolean isXmlExist = false;
			
			//未添加cache的xml
			for(SqlMapperBean no : noList) {
				if(ref.getRefNameSpace().equals(no.getNameSpace())) {
					isXmlExist = true;
					failRelation.append(no.getMapperName())
								.append(":")
								.append("应添加<cache type=\"org.mybatis.caches.ehcache.LoggingEhcache\"/>;");
					break;
				}
			}

			for(SqlMapperBean cache : cacheList) {
				if(cache.getNameSpace().equals(ref.getRefNameSpace())) {
					isXmlExist = true;
					boolean isTableExist = false;
					for(String name : cache.getRefTableName()) {
						if(name.equals(ref.getTableName())) {
							isTableExist = true;
							break;
						}
					}
					
					if(!isTableExist) {
						failRelation.append(cache.getMapperName())
									.append(":")
									.append("未关联表").append(ref.getTableName())
									.append(" 但").append(ref.getMapperName()).append("已设置为从缓存;");
					}
					break;
				}
			}
			
			for(String tableName : ref.getRefTableName()) {
				for(SqlMapperBean no : noList) {
					if(tableName.equals(no.getTableName())) {
						failRelation.append(no.getMapperName())
									.append(":")
									.append("应添加<cache-ref namespace=\"").append(ref.getRefNameSpace()).append("\"/>;");
						break;
					}
				}
				
				for(SqlMapperBean cache : cacheList) {
					if(tableName.equals(cache.getTableName()) && !cache.getNameSpace().equals(ref.getRefNameSpace())) {
						failRelation.append(ref.getMapperName())
									.append(":")
									.append("已关联").append(tableName).append("表 但")
									.append(cache.getMapperName()).append("的nameSpace与").append(ref.getRefNameSpace()).append("的cache-ref namespace不同;");
						break;
					}
				}
				
				for(SqlMapperBean thisRef : refList) {
					if(tableName.equals(thisRef.getTableName()) && !thisRef.getRefNameSpace().equals(ref.getRefNameSpace())) {
						failRelation.append(ref.getMapperName())
									.append(":")
									.append("已关联").append(tableName).append("表 但")
									.append(thisRef.getMapperName()).append("的cache-ref namespace与").append(ref.getRefNameSpace()).append("的cache-ref namespace不同;");
						break;
					}
				}
			}
			
			//主表不存在
			if(!isXmlExist) {
				failRelation.append(ref.getMapperName())
							.append(":")
							.append("nameSpace为").append(ref.getRefNameSpace()).append("的mapper文件不存在;");
				continue;
			}
		}

		replaceMap.put("successRelation","\"" + successRelation + "\"");
		replaceMap.put("failRelation","\"" + failRelation + "\"");
		return replaceMap;
	}
	
}
