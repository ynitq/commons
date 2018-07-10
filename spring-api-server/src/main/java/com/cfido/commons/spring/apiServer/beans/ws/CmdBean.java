package com.cfido.commons.spring.apiServer.beans.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * <pre>
 * 调用接口的命令的bean
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
public class CmdBean {

	/** 这个作为要调用方法的唯一标示，格式是：分类/方法名 */
	private String url;

	/** 表单数据 */
	private JSONObject form;

	/** 客户端为了方便识别接口调用的序列号 */
	private String sid;

	/** 收到的原始字符串 */
	private String originStr;

	public CmdBean() {

	}

	public CmdBean(String url, JSONObject form, String sid, String originStr) {

		this.url = url;
		this.form = form;
		this.sid = sid;
		this.originStr = originStr;

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public JSONObject getForm() {
		return form;
	}

	public void setForm(JSONObject form) {
		this.form = form;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getOriginStr() {
		return originStr;
	}

	public void setOriginStr(String originStr) {
		this.originStr = originStr;
	}

	/**
	 * 解析出表单
	 * 
	 * @param formClazz
	 * @return
	 */
	public <T> T parserForm(Class<T> formClazz) {
		if (this.form != null) {
			try {
				String str = JSON.toJSONString(this.form);
				return JSON.parseObject(str, formClazz);
			} catch (Throwable e) {
				return null;
			}
		} else {
			return null;
		}
	}

}
