package com.cfido.commons.beans.monitor;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 服务器回调客户端时，客户端返回的信息
 * </pre>
 * 
 * @author 梁韦江 2016年12月16日
 */
public class ClientInfoResponse {

	/** 这个响应生成的时间 */
	private long createTime;

	/** 从上次到现在共发生了多少次请求 */
	private int requestCount;

	/** 主程序的生成时间 */
	private long startClassBuildTime;

	/** 系统启动时间 */
	private long startTime;

	/** 磁盘信息 */
	private DiskInfoBean diskInfo = new DiskInfoBean();


	/** 操作系统信息 */
	private OsInfoBean osInfo = new OsInfoBean();

	/** 额外的信息 */
	private Map<String, Object> extInfo = new HashMap<>();

	public Map<String, Object> getExtInfo() {
		return extInfo;
	}

	public void setExtInfo(Map<String, Object> extInfo) {
		this.extInfo = extInfo;
	}

	public DiskInfoBean getDiskInfo() {
		return diskInfo;
	}

	public void setDiskInfo(DiskInfoBean diskInfo) {
		this.diskInfo = diskInfo;
	}

	public OsInfoBean getOsInfo() {
		return osInfo;
	}

	public void setOsInfo(OsInfoBean osInfo) {
		this.osInfo = osInfo;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getRequestCount() {
		return requestCount;
	}

	public void setRequestCount(int requestCount) {
		this.requestCount = requestCount;
	}

	public long getStartClassBuildTime() {
		return startClassBuildTime;
	}

	public void setStartClassBuildTime(long startClassBuildTime) {
		this.startClassBuildTime = startClassBuildTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

}
