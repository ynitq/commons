package com.cfido.commons.utils.utils;

import java.util.Calendar;

/**
 * <pre>
 * 时间键值的工具
 * </pre>
 * 
 * @author 梁韦江
 */
public class TimeKeyUtil {

	/** 获取当前的天的key */
	public static int getDayKey() {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int mon = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);

		return year * 10000 + mon * 100 + day;
	}

	/** 获取当前的小时的key */
	public static int getHourKey() {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int mon = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);

		return (year * 100 + mon) * 100 + day + hour;
	}

	/** 获取当前的月的key */
	public static int getMonthKey() {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int mon = c.get(Calendar.MONTH) + 1;

		return year * 100 + mon;
	}

	/** 获取当前小时 */
	public static int getHour() {
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		return hour;
	}

}
