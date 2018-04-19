package com.cfido.commons.spring.dict.inf.responses;

import java.util.Map;

import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.beans.apiServer.BaseResponse;

/**
 * <pre>
 * 对公开接口的内容
 * </pre>
 * 
 * @author 梁韦江 2016年11月14日
 */
@AComment("返回所有字典定义内容")
public class DictPublicResponse extends BaseResponse {

	@AComment("所有的数据")
	private Map<String, String> data;

	@AComment("服务器是否debug模式")
	private boolean debugMode;

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}
}
