package com.fastjavaframework.listener;

import com.fastjavaframework.FastjavaSpringBootConfig;
import com.fastjavaframework.annotation.Authority;
import com.fastjavaframework.listener.base.AbstractContextListener;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author wangshuli
 */
public class SystemSettingListener extends AbstractContextListener {

	@Override
	public void springContext(ApplicationContext applicationContext) {
		// 设置权限url
		if(FastjavaSpringBootConfig.optionsAuhority) {
			this.setAuhority(applicationContext);
		}
	}

	/**
	 * 设置权限url
	 * @param applicationContext
	 */
	private void setAuhority(ApplicationContext applicationContext) {
		for(String className : applicationContext.getBeanDefinitionNames()) {
			Class clz = applicationContext.getBean(className).getClass();

			// Controller bean
			if(null != clz.getAnnotation(RestController.class)) {

				// 类rest请求路径
				RequestMapping clzRmAnnotation = (RequestMapping)clz.getAnnotation(RequestMapping.class);
				Set<String> clzUris = new HashSet<>();
				if(null != clzRmAnnotation && clzRmAnnotation.value().length > 0) {
					clzUris.addAll(Arrays.asList(clzRmAnnotation.value()));
				}
				if(clzUris.size() == 0) {
					clzUris.add("");
				}

				for(Method method : clz.getMethods()) {
					// 有@Authority的方法
					Authority methodAuthorityAnnotation = method.getAnnotation(Authority.class);
					if(null != methodAuthorityAnnotation) {
						// 当前方法的权限
						StringBuilder methodAuthoritiesStr = new StringBuilder();
						Set<String> methodAuthorities = new HashSet<>();
						if(methodAuthorityAnnotation.login()) {
							methodAuthorities.add("login");
						}
						methodAuthorities.addAll(Arrays.asList(methodAuthorityAnnotation.authority()));

						// 当前方法的角色
						StringBuilder methodRolesStr = new StringBuilder();
						Set<String> methodRoles = new HashSet<>();
						methodRoles.addAll(Arrays.asList(methodAuthorityAnnotation.role()));
						for(String methodRole : methodRoles) {
							if(methodRolesStr.length() > 0) {
								methodRolesStr.append(",");
							}
							methodRolesStr.append(methodRole);
						}

						// 当前方法的rest参数
						Set<String> methodUris = new HashSet<>();
						Set<String> methodMethods = new HashSet<>();
						StringBuilder methodMethodsStr = new StringBuilder();

						Set<String> methodUriMethod = new HashSet<>();
						Set<String> methodUriRoles = new HashSet<>();
						RequestMapping methodRmAnnotation = method.getAnnotation(RequestMapping.class);
						if(null != methodRmAnnotation) {
							// 请求类型
							RequestMethod[] requestMethods = methodRmAnnotation.method();
							// 请求路径
							String[] requestValues = methodRmAnnotation.value();

							// 拼接路径（请求类型:请求路径:角色）
							for(RequestMethod requestMethod : requestMethods) {
								methodMethods.add(requestMethod.name());
								if(methodMethodsStr.length() > 0) {
									methodMethodsStr.append(",");
								}
								methodMethodsStr.append(requestMethod.name());

								if(requestValues.length == 0) {
									for(String clzUri : clzUris) {
										methodUris.add(clzUri);
										methodUriMethod.add(
												new StringBuilder(requestMethod.name())
														.append(":")
														.append(clzUri).toString());
										methodUriRoles.add(
												new StringBuilder(requestMethod.name())
														.append(":")
														.append(clzUri)
														.append(":")
														.append(methodRolesStr).toString());
									}
								}
								for(String requestValue : requestValues) {
									for(String clzUri : clzUris) {
										methodUris.add(clzUri + requestValue);
										methodUriMethod.add(
												new StringBuilder(requestMethod.name())
														.append(":")
														.append(clzUri)
														.append(requestValue).toString());
										methodUriRoles.add(
												new StringBuilder(requestMethod.name())
														.append(":")
														.append(clzUri)
														.append(requestValue)
														.append(":")
														.append(methodRolesStr).toString());
									}
								}
							}
						}

						// 根据权限配置
						// 格式：key:权限 value:Set<请求类型:请求路径:角色>
						for(String methodAuthority : methodAuthorities) {
							SystemSetting.putAuthority(methodAuthority, methodUriRoles);

							if(methodAuthoritiesStr.length() > 0) {
								methodAuthoritiesStr.append(",");
							}
							methodAuthoritiesStr.append(methodAuthority);
						}

						/**
						 * 根据uri配置
						 * 格式：key:uri value:String<请求类型1:,请求类型2:角色1,角色2:权限1,权限2>
						 */
						for(String methodUri : methodUris) {
							SystemSetting.putAuthorityByUrl(methodUri, methodMethodsStr.append(":")
									.append(methodRolesStr).append(":")
									.append(methodAuthoritiesStr).toString());
						}

						/**
						 * 根据角色配置
						 * 格式：key:角色 value:Set<请求类型:请求uri>
						 */
						for(String methodRole : methodRoles) {
							SystemSetting.putAuthorityByRole(methodRole, methodUriMethod);
						}
					}
				}
			}
		}
	}

}
