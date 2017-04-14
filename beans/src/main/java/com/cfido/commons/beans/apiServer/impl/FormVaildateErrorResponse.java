package com.cfido.commons.beans.apiServer.impl;

import java.util.List;

import com.cfido.commons.beans.exceptions.ValidateFormException;

/**
 * <pre>
 * 表单校验不通过的返回对象。
 *
 * </pre>
 * 
 * @author 梁韦江
 *  2016年6月28日
 */
public class FormVaildateErrorResponse extends CommonErrorResponse {
	/**
	 * 表单校验不通过的内容
	 */
	private final List<FormValidateErrorInfoBean> errors;

	public FormVaildateErrorResponse(ValidateFormException e) {
		super(e);
		this.errors = e.getErrors();
	}

	public List<FormValidateErrorInfoBean> getErrors() {
		return errors;
	}

}
