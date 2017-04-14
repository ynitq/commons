package com.cfido.commons.apiServer.adapter;

import com.cfido.commons.apiServer.beans.ApiServerInitException;

/**
 * <pre>
 * API server 接入器的基类。
 * 
 * 主要功能时创建 各个接口定义方法的map
 * </pre>
 * 
 * @author 梁韦江
 *  2016年6月30日
 */
public abstract class BaseApiServerAdapter {

	/**
	 * 将API接口放入Map
	 * 
	 * @param implObj
	 *            Object
	 * @throws ApiServerInitException
	 *             ApiServerInitException
	 */
	protected void addImplToMap(Object implObj) throws ApiServerInitException {
		ApiMapContainer.getInstance().addImplToMap(implObj);
	}

}
