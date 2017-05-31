package com.fastjavaframework.util;

import java.awt.Color;

/**
 * 颜色工具类
 */
public class ColorUtil {

	/**
	 * 16进制颜色转color对象
	 * @param str
	 * @return
	 */
	public static Color String2Color(String str) {  
        int i = Integer.parseInt(str.substring(1), 16);  
        return new Color(i);  
    } 
	
	/**
	 * color对象转RGB
	 * @param color
	 * @return
	 */
	public static String Color2String(Color color) {  
        String R = Integer.toHexString(color.getRed());  
        R = R.length() < 2 ? ('0' + R) : R;  
        String B = Integer.toHexString(color.getBlue());  
        B = B.length() < 2 ? ('0' + B) : B;  
        String G = Integer.toHexString(color.getGreen());  
        G = G.length() < 2 ? ('0' + G) : G;  
        return '#' + R + B + G;  
    }  
	
}
