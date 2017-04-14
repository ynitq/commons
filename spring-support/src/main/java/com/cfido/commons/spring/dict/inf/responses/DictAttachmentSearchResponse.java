package com.cfido.commons.spring.dict.inf.responses;

import java.util.List;

import com.cfido.commons.annotation.api.AMock;
import com.cfido.commons.beans.apiServer.BaseResponse;

/**
 * <pre>
 * 根据关键词搜索附件的结果
 * </pre>
 * 
 * @author 梁韦江 2016年11月16日
 */
public class DictAttachmentSearchResponse extends BaseResponse {

	private List<DictAttachmentVo> list;

	@AMock(size = 2)
	public List<DictAttachmentVo> getList() {
		return list;
	}

	public void setList(List<DictAttachmentVo> list) {
		this.list = list;
	}

}
