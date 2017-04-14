package com.cfido.commons.beans.exceptions;

import java.util.List;

import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.apiServer.impl.FormValidateErrorInfoBean;

/**
 * <pre>
 * 表单验证错误。验证方法在 BinderUtil中
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月24日
 */
public class ValidateFormException extends BaseApiException {

	private static final long serialVersionUID = 1L;

	/** 被验证的表单 */
	private final Object form;

	/** 错误信息 */
	private final List<FormValidateErrorInfoBean> errors;

	public ValidateFormException(Object form, List<FormValidateErrorInfoBean> errors) {
		this.form = form;
		this.errors = errors;
	}

	public Object getForm() {
		return form;
	}

	public List<FormValidateErrorInfoBean> getErrors() {
		return errors;
	}

	@Override
	public String getErrorMsg() {
		StringBuffer sb = new StringBuffer();

		for (FormValidateErrorInfoBean info : errors) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(info.getErrorMsg());
		}

		return sb.toString();
	}

}
