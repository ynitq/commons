package com.cfido.commons.beans.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.cfido.commons.annotation.api.AForm;
import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.beans.form.IdForm;

/**
 * <pre>
 * 用于在后台重置管理员账号密码的表单
 * </pre>
 * 
 * @author 梁韦江 2016年12月7日
 */
@AForm
public class ResetPasswordForm extends IdForm {

	@NotNull(message = "密码不能为空")
	@Size(min = 6, message = "密码长度不能小于6位")
	private String newPassword;

	public String getNewPassword() {
		return newPassword;
	}

	@AComment(value = "新密码，长度不能小于6位")
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
