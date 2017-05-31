package com.fastjavaframework.interceptor;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fastjavaframework.UserSession;
import com.fastjavaframework.annotation.Approve;
import com.fastjavaframework.annotation.Create;
import com.fastjavaframework.annotation.Delete;
import com.fastjavaframework.annotation.Read;
import com.fastjavaframework.annotation.Update;
import com.fastjavaframework.annotation.Upload;
import com.fastjavaframework.util.CommonUtil;

/**
 * 权限拦截器
 * 方法上包含'@Create' '@Update' '@Delete' '@Read' '@Upload' '@Approve' 注解
 * api路径包含库中open_id
 * 满足以上条件则有权限
 */
public class AuthorityInterceptor extends HandlerInterceptorAdapter {

	@Override
	@Create
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		
		if(null != userSession) {
			//权限
			Map<String,String> menuAuthority = userSession.getMenuAuthority();
			
			//获取方法
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			Method method = handlerMethod.getMethod();
			
			//库中open_id(key)与api路径对比,并判断方法是否与相应的权限注解
			for(String key : menuAuthority.keySet()) {
				String value = menuAuthority.get(key);
				
				if(request.getRequestURI().indexOf(key) != -1 && ("all".equals(value)
						|| ("read".equals(value) && null != method.getAnnotation(Read.class))
						|| ("create".equals(value) && null != method.getAnnotation(Create.class))
						|| ("update".equals(value) && null != method.getAnnotation(Update.class))
						|| ("delete".equals(value) && null != method.getAnnotation(Delete.class))
						|| ("upload".equals(value) && null != method.getAnnotation(Upload.class))
						|| ("Approve".equals(value) && null != method.getAnnotation(Approve.class)) )) {
					return true;
				}
			}
		}
		
		CommonUtil.setResponseReturnValue(response, HttpServletResponse.SC_UNAUTHORIZED, "无权限进行此操作！");
		return false;
	}
 
}
 