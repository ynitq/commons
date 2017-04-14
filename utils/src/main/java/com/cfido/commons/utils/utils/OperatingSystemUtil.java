package com.cfido.commons.utils.utils;

import java.lang.management.ManagementFactory;

/**
 * <pre>
 * 操作系统系统工具
 * </pre>
 * 
 * @author 梁韦江 2016年12月17日
 */
public class OperatingSystemUtil {

	/**
	 * 获得系统负载情况
	 */
	public static double getSystemLoadAverage() {
		return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
	}

	/**
	 * 获取已经分配的内存总数
	 */
	public static long getTotalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	/**
	 * 获取可分配的内存总数
	 */
	public static long getFreeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

	/**
	 * 获取可分配的最大内存数
	 */
	public static long getMaxMemory() {
		return Runtime.getRuntime().maxMemory();
	}

	/**
	 * 获取已经使用的内存数
	 */
	public static long getUsedMemory() {
		return getTotalMemory() - getFreeMemory();
	}

}
