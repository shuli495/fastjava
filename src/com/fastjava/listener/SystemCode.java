package com.fastjava.listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.fastjava.exception.ThrowException;

/**
 * 读取字典表
 */
public class SystemCode extends ContextLoader {
	private static Logger logger = LoggerFactory.getLogger(SystemCode.class);
	
	public static Map<String, CodeBean> CODE = new HashMap<>();

	@Override
	public void runBeforeContext(ApplicationContext context) {
	}

	@Override
	public void runAferContext(ApplicationContext context) {
		try {
			logger.info("---------读取字典表---------");
			DataSource ds = (DataSource) context.getBean("dataSource");
			PreparedStatement ps = ds.getConnection().prepareStatement("select id,parent_id,`group`,name,`code` from sys_code where enable='1' order by id,parent_id,sequence");
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				int id = rs.getInt("id");
				int parentId = rs.getInt("parent_id");
				String group = rs.getString("group");
				String name = rs.getString("name");
				String code = rs.getString("code");
				CODE.put(group, new CodeBean(id, parentId, name, code));

				logger.info("--->" + id + "," + parentId + "," + group + "," + name + "," + code);
			}
			logger.info("---------读取字典表结束---------");
		} catch (SQLException e) {
			logger.info("---------读取字典表失败---------");
			throw new ThrowException("读取字典表失败：" + e.getMessage());
		}
	}

	class CodeBean {
		private int id;
		private int parentId;
		private String name;
		private String code;
		
		public CodeBean(int id,int parentId,String name,String code) {
			this.id = id;
			this.parentId = parentId;
			this.name = name;
			this.code = code;
		}
		
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		
		public int getParentId() {
			return parentId;
		}
		
		public void setParentId(int parentId) {
			this.parentId = parentId;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getCode() {
			return code;
		}
		
		public void setCode(String code) {
			this.code = code;
		}
	}
	
}