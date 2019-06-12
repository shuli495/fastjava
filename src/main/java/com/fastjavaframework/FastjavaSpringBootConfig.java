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

        /**
         * response状态是否一直返回200
         */
        public static boolean isResponseStatus200;

        public static void setMessage(String message) {
            Exception.message = message;
        }

        public static void setIsResponseStatus200(boolean isResponseStatus200) {
            Exception.isResponseStatus200 = isResponseStatus200;
        }
    }

    public void setException(Exception exception) {
        FastjavaSpringBootConfig.exception = exception;
    }

    public void setOptionsAuhority(boolean optionsAuhority) {
        FastjavaSpringBootConfig.optionsAuhority = optionsAuhority;
    }
}
