package com.fastjavaframework.util;

import java.text.SimpleDateFormat;
import java.util.Base64;
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

	/**
	 * 64进制uuid
	 * @return
     */
	public static String uuid64() {
		java.util.UUID uuid = java.util.UUID.randomUUID();

		byte[] byUuid = new byte[16];
		long least = uuid.getLeastSignificantBits();
		long most = uuid.getMostSignificantBits();
		long2bytes(most, byUuid, 0);
		long2bytes(least, byUuid, 8);
		String compressUUID = Base64.getUrlEncoder().encodeToString(byUuid);
		return compressUUID.substring(0, compressUUID.length()-2);
	}

	private static void long2bytes(long value, byte[] bytes, int offset) {
		for (int i = 7; i > -1; i--) {
			bytes[offset++] = (byte) ((value >> 8 * i) & 0xFF);
		}
	}
}
