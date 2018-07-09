package com.cfido.commons.spring.jmxInWeb.inf.response;

import java.util.LinkedList;
import java.util.List;

import com.cfido.commons.beans.apiServer.BaseResponse;
import com.cfido.commons.spring.jmxInWeb.models.DomainVo;

/**
 * <pre>
 * 返回 所有的mbean
 * </pre>
 * 
 * @author 梁韦江 2017年4月25日
 */
public class JwMBeanListResponse extends BaseResponse {

	private List<DomainVo> list = new LinkedList<>();

	public List<DomainVo> getList() {
		return list;
	}

	public void setList(List<DomainVo> list) {
		this.list = list;
	}

}
