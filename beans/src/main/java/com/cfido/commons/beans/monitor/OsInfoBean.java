package com.cfido.commons.beans.monitor;

/**
 * <pre>
 * 操作系统信息
 * </pre>
 * 
 * @author 梁韦江 2016年12月17日
 */
public class OsInfoBean {
	/** 系统负载 */
	private double systemLoadAverage;
	/** 已分配内存，已经分配不代表已经使用 */
	private long totalMemory;
	/** 还可分配的内存 */
	private long freeMemory;
	/** 已经使用的内存 */
	private long usedMemory;
	/** 最大可分配内存 */
	private long maxMemory;

	public double getSystemLoadAverage() {
		return systemLoadAverage;
	}

	public void setSystemLoadAverage(double systemLoadAverage) {
		this.systemLoadAverage = systemLoadAverage;
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}

	public long getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public long getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(long usedMemory) {
		this.usedMemory = usedMemory;
	}

	public long getMaxMemory() {
		return maxMemory;
	}

	public void setMaxMemory(long maxMemory) {
		this.maxMemory = maxMemory;
	}

}
