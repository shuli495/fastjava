package com.fastjavaframework;

import java.util.Properties;

/**
 * spring配置文件注入次bean，通过此类，java类中直接获取properties中的参数
 */
public class Setting {

    private static Properties properties;

    public static Properties getAll() {
        return properties;
    }

    /**
     * 如果value是{XXX}格式，则取系统环境变量
     * @param key
     * @return
     */
    public static String getPropertyByEnv(String key) {
        String value = properties.getProperty(key);

        if(value.startsWith("{") && value.endsWith("}")) {
            return System.getenv(value);
        } else {
            return value;
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperties(Properties properties) {
        Setting.properties = properties;
    }
}
