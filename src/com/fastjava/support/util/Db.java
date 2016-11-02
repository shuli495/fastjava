package com.fastjava.support.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import com.fastjava.exception.ThrowException;

/**
 * 读取数据库信息
 */
public class Db {

	/**
	 * 获取数据库信息
	 * @param WebApplicationContext
	 * @return DatabaseMetaData
	 */
	public DatabaseMetaData getDb(ApplicationContext ctx) {
		DataSource ds = ctx.getBean(DataSource.class);
		Connection conn = null;
		try {
			conn = ds.getConnection();
			return conn.getMetaData();
		} catch (SQLException e) {
			throw new ThrowException("读取数据库表错误：" + e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new ThrowException("关闭数据库连接错误：" + e.getMessage());
			}
		}
	}
	
	/**
	 * 获取数据库表信息
	 * @param databaseMetaData
	 * @return 表集合
	 */
	public ResultSet getTables(DatabaseMetaData databaseMetaData) {
		try {
			return databaseMetaData.getTables(null, "%", "%", new String[] { "TABLE" });
		} catch (SQLException e) {
			throw new ThrowException("读取数据库错误：" + e.getMessage());
		}
	}
	
	/**
	 * 获取数据库表名
	 * @param databaseMetaData
	 * @return 据库表名set集合
	 */
	public Set<String> getTablesName(DatabaseMetaData databaseMetaData) {
		Set<String> tableNames = new HashSet<>();
		
		try {
			ResultSet tableRs = getTables(databaseMetaData);
			while (tableRs.next()) {
				tableNames.add(tableRs.getString("TABLE_NAME").toUpperCase());
			}
		} catch (Exception e) {
			throw new ThrowException("读取数据库错误：" + e.getMessage());
		}
		
		return tableNames;
	}
	
	/**
	 * 获取当前表注释
	 * @param databaseMetaData
	 * @param name 当前表明
	 * @return 当前表注释
	 */
	public String getTableRemarks(ResultSet tableRs, String name) {
		try {
			while (tableRs.next()) {
				if(name.equals(tableRs.getString("TABLE_NAME"))) {
					return tableRs.getString("REMARKS");
				}
			}
		} catch (Exception e) {
			throw new ThrowException("读取数据库错误：" + e.getMessage());
		}
		return "";
	}
}
