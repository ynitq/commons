package com.cfido.commons.spring.jmxInWeb.inf.response;

import com.cfido.commons.beans.apiServer.BaseResponse;
import com.cfido.commons.spring.jmxInWeb.models.MBeanVo;

/**
 * <pre>
 * 获取一个Mbean信息的响应
 * </pre>
 * 
 * @author 梁韦江 2017年4月25日
 */
public class JwMBeanInfoResponse extends BaseResponse {

	private MBeanVo info;

	public MBeanVo getInfo() {
		return info;
	}

	public void setInfo(MBeanVo info) {
		this.info = info;
	}

}
