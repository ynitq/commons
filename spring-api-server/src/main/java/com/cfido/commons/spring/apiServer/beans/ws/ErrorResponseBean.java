package com.cfido.commons.spring.apiServer.beans.ws;

import java.util.Map;

import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.utils.utils.ExceptionUtil;

/**
 * <pre>
 * 调用接口 失败时的返回
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
public class ErrorResponseBean extends BaseResponseBean {

	@AComment("错误代码")
	private final String errorCode;

	@AComment("错误消息")
	private final String errorMsg;

	@AComment("错误额外的数据")
	private final Map<String, Object> errorData;

	public ErrorResponseBean(CmdBean cmd, Throwable e) {
		super(-1, cmd);

		this.errorCode = e.getClass().getName();

		if (e instanceof BaseApiException) {

			BaseApiException e1 = (BaseApiException) e;
			this.errorMsg = e1.getErrorMsg();

			// 同时设置额外的数据进去
			this.errorData = ExceptionUtil.getExDataFromApiException(e1);

		} else {
			// 其他错误时，返回系统错误
			this.errorMsg = e.getMessage();
			this.errorData = null;
		}

	}

	public Map<String, Object> getErrorData() {
		return errorData;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

}
