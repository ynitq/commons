package com.cfido.commons.beans.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.cfido.commons.annotation.api.AForm;
import com.cfido.commons.annotation.api.AMock;
import com.cfido.commons.annotation.bean.AComment;

/**
 * <pre>
 * 用于验证电话号码的表单
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
@AForm
public class VerifyPhoneForm extends PhoneForm {

	@AComment(value = "4位短信验证码")
	@NotNull(message = "验证码不能为空")
	@Size(min = 4, max = 3, message = "验证码为3位")
	private String code;

	public String getCode() {
		return code;
	}

	@AMock("1234")
	public void setCode(String code) {
		this.code = code;
	}
}
