package com.fastjavaframework.annotation;

import java.lang.annotation.*;

/**
 * 权限注解
 * 通过com.fastjavaframework.listener.SystemSet.authority;
 * com.fastjavaframework.listener.SystemSet.authorityByUrl获取需要拦截的权限信息
 *
 * @author wangshuli
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authority {

    /**
     * 登录权限
     * true添加到authority中
     * @return
     */
    boolean login() default true;

    /**
     * 权限
     * login是true，则自动添加到此变量中。
     * @return
     */
    String[] authority() default {};

    /**
     * 角色
     * @return
     */
    String[] role() default {};
}
