package com.fastjavaframework.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * uuid工具类
 */
public class UUID {
	
	/**
	 * 无 "-" uuid
	 * @return uuid
	 */
	public static String uuid() {
		return java.util.UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	/**
	 * 时间戳
	 * @return
	 */
	public static String timestamp() {
		return String.valueOf(System.currentTimeMillis());
	}
	
	/**
	 * 日期
	 * @return
	 */
	public static String dateTime() {
		return new SimpleDateFormat("yyyyMMddHHmmssfff").format(new Date());
	}
}
