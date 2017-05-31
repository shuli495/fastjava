package com.fastjavaframework.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {

	/**
	 * 日期偏移计算
	 * @param date 要偏移的日期
	 * @param type 要偏移的类型 DAY日
	 * @param time 要便宜的时间
	 * @return Date
	 */
	public Date offset(Date date,String type, int time) {
		Calendar c = Calendar.getInstance();  
        c.setTime(date);   //设置当前日期  
        
        if("DAY".equals(type)) {
            c.add(Calendar.DATE, time);
        }
        return c.getTime(); //结果  
	}
	
	/**
	 * 格式化日期
	 * @param pattern 日期格式
	 * @param date	  日期
	 * @return
	 */
	public static String format(String pattern, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

}
