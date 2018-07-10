package com.cfido.commons.spring.apiServer.ws;

import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.spring.apiServer.beans.ws.BaseSocketResponse;
import com.cfido.commons.spring.apiServer.service.ApiMethodInfo;
import com.cfido.commons.spring.apiServer.ws.WsLoginContext.ConnHandler;

/**
 * <pre>
 * ws接口默认额外的执行器
 * </pre>
 * 
 * @author 梁韦江
 */
public interface IWsExtSecurityService {

	/**
	 * 在api server 的对应方法被执行前，先执行这个方法。
	 * 
	 * @param handler
	 *            连接信息
	 * @param apiMethodInfo
	 *            准备调用的api的方法
	 * @throws BaseApiException
	 *             如果抛错误，就不会执行apiMethodInfo
	 */
	void onBeforeInvoke(ConnHandler handler, ApiMethodInfo<BaseSocketResponse> apiMethodInfo)
			throws BaseApiException;

}
