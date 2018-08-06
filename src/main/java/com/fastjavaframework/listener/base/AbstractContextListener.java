package com.fastjavaframework.listener.base;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 在application中springApplication.addListeners,加载完配置后运行
 *
 * @author wangshuli
 */
public abstract class AbstractContextListener implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent context) {
		springContext(context.getApplicationContext());
	}
	
	/**
	 * springboot上下文
	 * @param applicationContext
	 */
	public abstract void springContext(ApplicationContext applicationContext);

}
