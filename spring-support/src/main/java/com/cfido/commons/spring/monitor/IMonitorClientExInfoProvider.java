package com.cfido.commons.spring.monitor;

import com.cfido.commons.beans.monitor.ClientInfoResponse;
import com.cfido.commons.beans.monitor.ServerRightsBean;

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

	/** 获取这个服务器的权限 */
	public ServerRightsBean getRights();

}
