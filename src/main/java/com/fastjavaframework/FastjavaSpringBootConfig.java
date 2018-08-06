package com.fastjavaframework;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author wangshuli
 */
@ServletComponentScan
@ComponentScan("com.fastjavaframework")
@ConfigurationProperties(prefix=FastjavaSpringBootConfig.FASTJAVA_PREFIXS)
public class FastjavaSpringBootConfig {
    public static final String FASTJAVA_PREFIXS = "fastjava";

    /**
     * 异常设置
     */
    public static Exception exception;

    /**
     * 是否设置全选url
     */
    public static boolean optionsAuhority;

    public static class Exception {
        /**
         * 自定义错误信息
         */
        public static String message;
    }

    public void setException(Exception exception) {
        FastjavaSpringBootConfig.exception = exception;
    }

    public void setOptionsAuhority(boolean optionsAuhority) {
        FastjavaSpringBootConfig.optionsAuhority = optionsAuhority;
    }
}
