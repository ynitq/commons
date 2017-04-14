package com.cfido.commons.beans.monitor;

/**
 * <pre>
 * 磁盘信息
 * </pre>
 * 
 * @author 梁韦江 2016年12月16日
 */
public class DiskInfoBean {

	private long freeSpace;
	private long totalSpace;
	private long usableSpace;

	public long getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(long freeSpace) {
		this.freeSpace = freeSpace;
	}

	public long getTotalSpace() {
		return totalSpace;
	}

	public void setTotalSpace(long totalSpace) {
		this.totalSpace = totalSpace;
	}

	public long getUsableSpace() {
		return usableSpace;
	}

	public void setUsableSpace(long usableSpace) {
		this.usableSpace = usableSpace;
	}

}
