package com.cfido.commons.spring.apiServer.beans.ws;

import com.cfido.commons.annotation.bean.AComment;

/**
 * <pre>
 * 调用接口成功时的返回
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
public class SuccessResponseBean extends BaseResponseBean {

	@AComment("接口调用的返回数据")
	private final BaseSocketResponse data;

	public SuccessResponseBean(CmdBean cmd, BaseSocketResponse data) {
		super(0, cmd);
		this.data = data;
	}

	public BaseSocketResponse getData() {
		return data;
	}

}
