package com.fastjava.listener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 加载项目监听 用于加载项目时实行业务处理
 */
public abstract class ContextLoader implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent context) {
		if (context.getApplicationContext().getParent() == null) {
			runBeforeContext(context.getApplicationContext());
		} else {
			runAferContext(context.getApplicationContext());
		}
	}
	
	/**
	 * spring上下文
	 * @param context
	 */
	public abstract void runBeforeContext(ApplicationContext context);
	
	/**
	 * springmvc上下文
	 * @param context
	 */
	public abstract void runAferContext(ApplicationContext context);

}
