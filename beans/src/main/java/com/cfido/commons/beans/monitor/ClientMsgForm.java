package com.cfido.commons.beans.monitor;

import com.cfido.commons.annotation.api.AForm;

/**
 * <pre>
 * 发送数据给服务器端的表单
 * </pre>
 * 
 * @author 梁韦江 2016年12月19日
 */
@AForm
public class ClientMsgForm extends BaseClientSignForm {

	/** 如果有信息时，这表示信息的等级 */
	private int msgType;

	/**
	 * 客户端信息的json字符串
	 * 
	 * @see ClientInfoResponse
	 */
	private String clientInfo;

	/**
	 * 客户端的权限
	 * 
	 * @see ServerRightsBean
	 */
	private String rightStr;

	/** 向服务器发送的消息，可以为空，为空时，服务器不记录 */
	private String msg;

	public String getMsg() {
		return msg;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(String clientInfo) {
		this.clientInfo = clientInfo;
	}

	public String getRightStr() {
		return rightStr;
	}

	public void setRightStr(String rightStr) {
		this.rightStr = rightStr;
	}

}
