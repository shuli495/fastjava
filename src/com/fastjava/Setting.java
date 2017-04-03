package com.fastjava;

import java.util.Properties;

/**
 * spring配置文件注入次bean，通过此类，java类中直接获取properties中的参数
 */
public class Setting {

    private static Properties properties;

    public static Properties getAll() {
        return properties;
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperties(Properties properties) {
        Setting.properties = properties;
    }
}
