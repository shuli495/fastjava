package com.fastjavaframework.base;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import com.fastjavaframework.page.Page;
import com.fastjavaframework.page.PageResult;
import com.fastjavaframework.util.ValidationUtils;

/**
 * 基础Dao类
 * 根据参数调用mapper文件中的sql
 *
 * @author wangshuli
 */
public class SqlDao extends SqlSessionDaoSupport {

	/**
	 * mapper文件的namespace
	 */
	protected String mapperName;
	
	/**
	 * 设置mapper XML的namespace
	 */
	protected SqlDao() {
		this.mapperName = this.getClass().getName() + "Mapper.";
	}
	
	/**
	 * 设置mapper命名空间
	 * @param nameSpace
	 */
	protected void mapperNameSpace(String nameSpace) {
		this.mapperName = nameSpace;
	}

	@Resource
	@Override
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory){
		super.setSqlSessionFactory(sqlSessionFactory);
	}
	
	/**
	 * sql执行类
	 * @return sql
	 */
	protected Sql sql() {
		return new Sql();
	}
	
	protected class Sql {
		
		/**
		 * 插入
		 * 调用mapperXml中id为sqlId的insert sql
		 * @param sqlId
		 * @param bean
		 */
		public int insert(String sqlId, Object bean) {
			//数据校验
			ValidationUtils.bean(bean);
			
			return getSqlSession().insert(mapperName + sqlId, bean);
		}
		
		/**
		 * 更新
		 * 调用mapperXml中id为sqlId的update sql
		 * 调用mapperXml中id为sqlId的sql
		 * @param sqlId
		 * @param bean
		 */
		public int update(String sqlId, Object bean) {
			return getSqlSession().update(mapperName + sqlId, bean);
		}
		
		/**
		 * 删除
		 * 调用mapperXml中id为sqlId的delete sql
		 * @param sqlId
		 * @param obj
		 */
		public int delete(String sqlId, Object obj) {
			return getSqlSession().delete(mapperName + sqlId, obj);
		}
		
		/**
		 * 查询
		 * 调用mapperXml中id为sqlId的select sql
		 * @param sqlId
		 * @param obj
		 * @return bean
		 */
		public <T> T selectOne(String sqlId, Object obj) {
			return getSqlSession().selectOne(mapperName + sqlId, obj);
		}
		
		/**
		 * 列表查询
		 * 调用mapperXml中id为sqlId的select sql
		 * @param sqlId
		 * @param obj
		 * @return List<bean>
		 */
		public <E> List<E> selectList(String sqlId, Object obj) {
			return getSqlSession().selectList(mapperName + sqlId, obj);
		}
		
		/**
		 * 列表查询 无条件
		 * 调用mapperXml中id为sqlId的select sql
		 * @param sqlId
		 * @return List<bean>
		 */
		public <E> List<E> selectList(String sqlId) {
			return getSqlSession().selectList(mapperName + sqlId);
		}
		
		/**
		 * 分页查询
		 * 调用mapperXml中id为sqlId的select sql
		 * @param sqlId
		 * @param params
		 * @param page
		 * @return PageResult
		 */
		public PageResult selectPage(String sqlId, Object params, Page page) {
			page = page == null ? new Page() : page;

			return new PageResult(page,
						getSqlSession().selectList(mapperName + sqlId, params, page));
		}
	}
}
