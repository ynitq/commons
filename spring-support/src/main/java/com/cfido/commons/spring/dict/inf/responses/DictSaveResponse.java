package com.cfido.commons.spring.dict.inf.responses;

import com.cfido.commons.beans.apiServer.BaseResponse;

/**
 * <pre>
 * 保存字典的返回
 * </pre>
 * 
 * @author 梁韦江 2016年11月17日
 */
public class DictSaveResponse extends BaseResponse {

	private DictVo data;

	public DictVo getData() {
		return data;
	}

	public void setData(DictVo data) {
		this.data = data;
	}

}
