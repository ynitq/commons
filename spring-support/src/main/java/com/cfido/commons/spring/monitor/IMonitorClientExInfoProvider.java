package com.cfido.commons.spring.monitor;

import com.cfido.commons.beans.monitor.ClientInfoResponse;

/**
 * <pre>
 * 额外信息提供者
 * </pre>
 * 
 * @author 梁韦江
 */
public interface IMonitorClientExInfoProvider {

	/** 更新额外的信息 */
	public void updateExInfo(ClientInfoResponse response, boolean resetRequestCounter);

}
