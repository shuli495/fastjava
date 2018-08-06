package com.fastjavaframework.util;

import java.awt.Color;

/**
 * 颜色工具类
 *
 * @author wangshuli
 */
public class ColorUtil {

	/**
	 * 16进制颜色转color对象
	 * @param str
	 * @return
	 */
	public static Color string2Color(String str) {
        int i = Integer.parseInt(str.substring(1), 16);  
        return new Color(i);  
    } 
	
	/**
	 * color对象转RGB
	 * @param color
	 * @return
	 */
	public static String color2String(Color color) {
        String r = Integer.toHexString(color.getRed());
        r = r.length() < 2 ? ('0' + r) : r;
        String b = Integer.toHexString(color.getBlue());
        b = b.length() < 2 ? ('0' + b) : b;
        String g = Integer.toHexString(color.getGreen());
        g = g.length() < 2 ? ('0' + g) : g;
        return '#' + r + b + g;
    }  
	
}
