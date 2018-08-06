package com.fastjavaframework.listener.base;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 子类需加注解"@WebListener",启动容器时运行。
 *
 * @author wangshuli
 */
public interface ContextListener extends ServletContextListener {

    /**
     * 启动时调用
     * @param sce
     */
    @Override
    default void contextInitialized(ServletContextEvent sce) {
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        initialized(applicationContext);
    }

    /**
     * 停止时调用
     * @param sce
     */
    @Override
    default void contextDestroyed(ServletContextEvent sce) {
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        destroyed(applicationContext);
    }

    /**
     * 启动时调用
     * @param applicationContext
     */
    void initialized(ApplicationContext applicationContext);

    /**
     * 停止时调用
     * @param applicationContext
     */
    void destroyed(ApplicationContext applicationContext);
}
