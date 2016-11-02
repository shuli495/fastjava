package com.fastjava.base;

import java.util.List;

import com.fastjava.page.Page;
import com.fastjava.page.PageResult;

@SuppressWarnings("unchecked")
public class BaseDao<B extends BaseBean> extends SqlDao {

	/**
	 * 插入
	 * mapperXml中需配置id为"insert"的sql
	 * @param bean
	 */
	public int baseInsert(B bean) {
		return super.sql().insert("insert", bean);
	}

	/**
	 * 批量插入
	 * mapperXml中需配置id为"insertBatch"的sql
	 * @param list<bean>
	 */
	public int baseInsertBatch(List<B> list) {
		if(null == list || list.size() == 0) {
			return 0;
		}
		
		return super.sql().insert("insertBatch", list);
	}

	/**
	 * 更新
	 * mapperXml中需配置id为"update"的sql
	 * @param bean
	 */
	public int baseUpdate(B bean) {
		return super.sql().update("update", bean);
	}

	/**
	 * 批量更新
	 * mapperXml中需配置id为"updateBatch"的sql
	 * @param list<bean>
	 */
	public int baseUpdateBatch(List<B> list) {
		if(null == list || list.size() == 0) {
			return 0;
		}
		
		return super.sql().update("updateBatch", list);
	}
	
	/**
	 * 逻辑删除
	 * mapperXml中需配置id为"deleteLogic"的sql
	 * @param bean
	 */
	public int baseDeleteLogic(B bean) {
		return super.sql().update("deleteLogic", bean);
	}

	/**
	 * 批量逻辑删除
	 * mapperXml中需配置id为"deleteBatchLogic"的sql
	 * @param list<bean>
	 */
	public int baseDeleteLogicBatch(List<B> list) {
		if(null == list || list.size() == 0) {
			return 0;
		}
		
		return super.sql().update("deleteLogicBatch", list);
	}

	/**
	 * 物理删除
	 * mapperXml中需配置id为"delete"的sql
	 * @param id
	 */
	public int baseDelete(Object id) {
		return super.sql().delete("delete", id);
	}

	/**
	 * 批量物理删除
	 * mapperXml中需配置id为"deleteBatch"的sql
	 * @param list id
	 */
	public int baseDeleteBatch(List<?> list) {
		if(null == list || list.size() == 0) {
			return 0;
		}
		
		return super.sql().delete("deleteBatch", list);
	}

	/**
	 * 根据id查询
	 * mapperXml中需配置id为"findById"的sql
	 * @param id
	 * @return bean
	 */
	public B baseFind(Object id) {
		return super.sql().selectOne("findById", id);
	}

	/**
	 * or条件查询
	 * mapperXml中需配置id为"queryByOr"的sql
	 * @param bean
	 * @return bean
	 */
	public B baseFindByOr(B bean) {
		List<B> list = baseQueryByOr(bean);
		return list.size() == 0 ? null : list.get(0);
	}

	/**
	 * and条件查询
	 * mapperXml中需配置id为"queryByAnd"的sql
	 * @param bean
	 * @return bean
	 */
	public B baseFindByAnd(B bean) {
		List<B> list = baseQueryByAnd(bean);
		return list.size() == 0 ? null : list.get(0);
	}

	/**
	 * 列表查询
	 * mapperXml中需配置id为"queryByOr"的sql
	 * @return List<bean>
	 */
	public List<B> baseQuery() {
		return baseQueryByOr(null);
	}

	/**
	 * or条件列表查询
	 * mapperXml中需配置id为"queryByOr"的sql
	 * @param bean
	 * @return List<bean>
	 */
	public List<B> baseQueryByOr(B bean) {
		return super.sql().selectList("queryByOr", bean);
	}

	/**
	 * and条件列表查询
	 * mapperXml中需配置id为"queryByAnd"的sql
	 * @param bean
	 * @return List<bean>
	 */
	public List<B> baseQueryByAnd(B bean) {
		return super.sql().selectList("queryByAnd", bean);
	}

	/**
	 * top列表查询
	 * mapperXml中需配置id为"queryByAnd"的sql
	 * @param top
	 * @return List<bean>
	 */
	public List<B> baseQueryTop(int top) {
		return (List<B>) baseQueryPage(new Page(top)).getDataList();
	}

	/**
	 * or条件top列表查询
	 * mapperXml中需配置id为"queryByOr"的sql
	 * @param bean
	 * @param top
	 * @return List<bean>
	 */
	public List<B> baseQueryTopByOr(B bean, int top) {
		bean.setPage(new Page(top));
		return (List<B>) baseQueryPageByOr(bean).getDataList();
	}

	/**
	 * and条件top查询
	 * mapperXml中需配置id为"queryByAnd"的sql
	 * @param bean
	 * @param top
	 * @return List<bean>
	 */
	public List<B> baseQueryTopByAnd(B bean, int top) {
		bean.setPage(new Page(top));
		return (List<B>) baseQueryPageByAnd(bean).getDataList();
	}

	/**
	 * 分页查询
	 * mapperXml中需配置id为"queryByAnd"的sql
	 * @param page
	 * @return PageResult
	 */
	public PageResult baseQueryPage(Page page) {
		return super.sql().selectPage("queryByAnd", null, page);
	}

	/**
	 * or条件分页查询
	 * mapperXml中需配置id为"queryByOr"的sql
	 * @param bean
	 * @return PageResult
	 */
	public PageResult baseQueryPageByOr(B bean) {
		return super.sql().selectPage("queryByOr", bean, bean.getPage());
	}

	/**
	 * and条件分页查询
	 * mapperXml中需配置id为"queryByAnd"的sql
	 * @param bean
	 * @return PageResult
	 */
	public PageResult baseQueryPageByAnd(B bean) {
		return super.sql().selectPage("queryByAnd", bean, bean.getPage());
	}
	
}
