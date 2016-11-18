package com.fastjava.page;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.RowBounds;

import com.fastjava.base.BaseBean;
import com.fastjava.exception.ThrowException;
import com.fastjava.util.VerifyUtils;

/**
 * 分页拦截器
 * 通过拦截<code>StatementHandler</code>的<code>prepare</code>方法，重写sql语句实现物理分页。
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class PageInterceptor implements Interceptor {
    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    private static final ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, DEFAULT_OBJECT_FACTORY,
                DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        // 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过下面的两次循环可以分离出最原始的的目标类)
        while (metaStatementHandler.hasGetter("h")) {
            Object object = metaStatementHandler.getValue("h");
            metaStatementHandler = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        }
        // 分离最后一个代理对象的目标类
        while (metaStatementHandler.hasGetter("target")) {
            Object object = metaStatementHandler.getValue("target");
            metaStatementHandler = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        }

        BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
        StringBuffer sql = new StringBuffer(boundSql.getSql());
        
        //分页
        Object rowBounds = metaStatementHandler.getValue("delegate.rowBounds");
        if(rowBounds instanceof Page) {
            // 重写sql
            String pageSql = sql + " LIMIT " + ((Page)rowBounds).getOffset() + "," + ((Page)rowBounds).getLimit();
            metaStatementHandler.setValue("delegate.boundSql.sql", pageSql);
            // 采用物理分页后，就不需要mybatis的内存分页了，所以重置下面的两个参数
            metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
            metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
            
            Connection connection = (Connection) invocation.getArgs()[0];  
            MappedStatement mappedStatement = (MappedStatement)metaStatementHandler.getValue("delegate.mappedStatement");
            setPageParameter(sql.toString(), connection, mappedStatement, boundSql, ((Page)rowBounds)); 
        }

        Object parameterObject = boundSql.getParameterObject();
    	if(null != parameterObject && BaseBean.class.isAssignableFrom(parameterObject.getClass())) {
            // 设置排序信息
        	Object order = metaStatementHandler.getValue("delegate.boundSql.parameterObject.order");
        	if(!VerifyUtils.isEmpty(order)) {
            	String sort = (String)metaStatementHandler.getValue("delegate.boundSql.parameterObject.sort");
        		sql.append(" ORDER BY ").append(order.toString()).append(" ").append(sort);
                metaStatementHandler.setValue("delegate.boundSql.sql", sql.toString());
        	}

            // 设置返回行数
            Object rowNum = metaStatementHandler.getValue("delegate.boundSql.parameterObject.rowNum");
            if(!VerifyUtils.isEmpty(rowNum)) {
                sql.append(" LIMIT ").append(rowNum.toString());
                metaStatementHandler.setValue("delegate.boundSql.sql", sql.toString());
            }
    	}
    
        // 将执行权交给下一个拦截器
        return invocation.proceed();
    }
    
    @Override
    public Object plugin(Object target) {
        // 当目标类是StatementHandler类型时，才包装目标类，否者直接返回目标本身,减少目标被代理的次数
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
    }
    
    /**
     * 从数据库里查询总的记录数并计算总页数，回写进分页参数<code>PageParameter</code>,这样调用 
     * 者就可用通过 分页参数<code>PageParameter</code>获得相关信息。
     * @param sql
     * @param connection
     * @param mappedStatement
     * @param boundSql
     * @param page
     */
    private void setPageParameter(String sql, Connection connection, MappedStatement mappedStatement,
            BoundSql boundSql, Page page) {
        // 记录总记录数
        String countSql = "select count(0) from (" + sql + ") as total";
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
            countStmt = connection.prepareStatement(countSql);
            BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql,
                    boundSql.getParameterMappings(), boundSql.getParameterObject());
            
            //由于该物理分页不支持mybatis的<foreach>标签，so对该分页做一下更改 
            Field metaParamsField = getFieldByFieldName(boundSql, "metaParameters");
			if (metaParamsField != null) {
				MetaObject mo = (MetaObject) getValueByFieldName(boundSql, "metaParameters");
				setValueByFieldName(countBS, "metaParameters", mo);
			}
			
            setParameters(countStmt, mappedStatement, countBS, boundSql.getParameterObject());
            rs = countStmt.executeQuery();
            int totalCount = 0;
            if (rs.next()) {
                totalCount = rs.getInt(1);
            }
            page.setTotalCount(totalCount);
            int totalPage = totalCount / page.getPageSize() + ((totalCount % page.getPageSize() == 0) ? 0 : 1);
            page.setTotalPage(totalPage);
        } catch (SQLException e) {
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
            	throw new ThrowException("分页总数查询，ResultSet关闭错误：" + e.getMessage());
            }
            try {
                countStmt.close();
            } catch (SQLException e) {
            	throw new ThrowException("分页总数查询，PreparedStatement关闭错误：" + e.getMessage());
            }
        }
    }
    
    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql,
            Object parameterObject) throws SQLException {
        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
        parameterHandler.setParameters(ps);
    }

    /**
     * 获取obj对象fieldName的Field
     * @param obj
     * @param fieldName
     * @return
     */
    public static Field getFieldByFieldName(Object obj, String fieldName) {
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
            	throw new ThrowException("分页查询，获取参数错误：" + e.getMessage());
            }
        }
        return null;
    }
 
    /**
     * 获取obj对象fieldName的属性值
     * @param obj
     * @param fieldName
     * @return
     */
    public static Object getValueByFieldName(Object obj, String fieldName) {
        Field field = getFieldByFieldName(obj, fieldName);
        Object value = null;
        if (field != null) {
            try {
	            if (field.isAccessible()) {
					value = field.get(obj);
	            } else {
	                field.setAccessible(true);
					value = field.get(obj);
	                field.setAccessible(false);
	            }
			} catch (IllegalArgumentException e) {
            	throw new ThrowException("分页查询，获取参数错误：" + e.getMessage());
			} catch (IllegalAccessException e) {
            	throw new ThrowException("分页查询，获取参数错误：" + e.getMessage());
			}
        }
        return value;
    }
 
    /**
     * 设置obj对象fieldName的属性值
     * @param obj
     * @param fieldName
     * @param value
     */
    public static void setValueByFieldName(Object obj, String fieldName, Object value) {
        Field field = getFieldByFieldName(obj, fieldName);
        try {
	        if (field.isAccessible()) {
	            field.set(obj, value);
	        } else {
	            field.setAccessible(true);
	            field.set(obj, value);
	            field.setAccessible(false);
	        }
		} catch (IllegalArgumentException e) {
        	throw new ThrowException("分页查询，获取参数错误：" + e.getMessage());
		} catch (IllegalAccessException e) {
        	throw new ThrowException("分页查询，获取参数错误：" + e.getMessage());
		}
    }

}