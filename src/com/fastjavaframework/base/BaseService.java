package com.fastjavaframework.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fastjavaframework.page.Page;
import com.fastjavaframework.page.PageResult;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BaseService<D extends BaseDao,B extends BaseBean> {
	
	@Autowired
	protected D dao;

	/**
	 * 插入
	 * @param bean
	 */
	public int baseInsert(B bean) {
		return dao.baseInsert(bean);
	}

	/**
	 * 批量插入
	 * @param list<bean>
	 */
	@Transactional
	public int baseInsertBatch(List<B> list) {
		return dao.baseInsertBatch(list);
	}

	/**
	 * 更新
	 * @param bean
	 */
	public int baseUpdate(B bean) {
		return dao.baseUpdate(bean);
	}

	/**
	 * 批量更新
	 * @param list<bean>
	 */
	@Transactional
	public int baseUpdateBatch(List<B> list) {
		return dao.baseUpdateBatch(list);
	}
	
	/**
	 * 逻辑删除
	 * @param bean
	 */
	public int baseDeleteLogic(B bean) {
		return dao.baseDeleteLogic(bean);
	}

	/**
	 * 批量逻辑删除
	 * @param list<bean>
	 */
	@Transactional
	public int baseDeleteLogicBatch(List<B> list) {
		return dao.baseDeleteLogicBatch(list);
	}

	/**
	 * 物理删除
	 * @param id
	 */
	public int baseDelete(Object id) {
		return dao.baseDelete(id);
	}

	/**
	 * 批量物理删除
	 * @param list id
	 */
	@Transactional
	public int baseDeleteBatch(List<?> list) {
		return dao.baseDeleteBatch(list);
	}

	/**
	 * 根据id查询
	 * @param id
	 * @return bean
	 */
	public B baseFind(Object id) {
		return (B) dao.baseFind(id);
	}

	/**
	 * or条件查询
	 * @param bean
	 * @return bean
	 */
	public B baseFindByOr(B bean) {
		return (B) dao.baseFindByOr(bean);
	}

	/**
	 * and条件查询
	 * @param bean
	 * @return bean
	 */
	public B baseFindByAnd(B bean) {
		return (B) dao.baseFindByAnd(bean);
	}

	/**
	 * 列表查询
	 * @return List<bean>
	 */
	public List<B> baseQuery() {
		return dao.baseQuery();
	}

	/**
	 * or条件列表查询
	 * @param bean
	 * @return List<bean>
	 */
	public List<B> baseQueryByOr(B bean) {
		return dao.baseQueryByOr(bean);
	}

	/**
	 * and条件列表查询
	 * @param bean
	 * @return List<bean>
	 */
	public List<B> baseQueryByAnd(B bean) {
		return dao.baseQueryByAnd(bean);
	}

	/**
	 * top列表查询
	 * @param top
	 * @return List<bean>
	 */
	public List<B> baseQueryTop(int top) {
		return dao.baseQueryTop(top);
	}

	/**
	 * or条件top列表查询
	 * @param bean
	 * @param top
	 * @return List<bean>
	 */
	public List<B> baseQueryTopByOr(B bean, int top) {
		return dao.baseQueryTopByOr(bean, top);
	}

	/**
	 * and条件top查询
	 * @param bean
	 * @param top
	 * @return List<bean>
	 */
	public List<B> baseQueryTopByAnd(B bean, int top) {
		return dao.baseQueryTopByAnd(bean, top);
	}

	/**
	 * 分页查询
	 * @param page
	 * @return PageResult
	 */
	public PageResult baseQueryPage(Page page) {
		return dao.baseQueryPage(page);
	}

	/**
	 * or条件分页查询
	 * @param bean
	 * @return PageResult
	 */
	public PageResult baseQueryPageByOr(B bean) {
		return dao.baseQueryPageByOr(bean);
	}

	/**
	 * and条件分页查询
	 * @param bean
	 * @return PageResult
	 */
	public PageResult baseQueryPageByAnd(B bean) {
		return dao.baseQueryPageByAnd(bean);
	}
	
}
