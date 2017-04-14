package com.cfido.commons.beans.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.cfido.commons.annotation.api.AForm;
import com.cfido.commons.annotation.bean.AComment;

/**
 * <pre>
 * 修改密码的表单，修改密码的时候，必须将原密码传过来，做一次验证
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月3日
 */
@AForm
public class ChangePasswordForm {

	/**
	 * 旧的密码
	 */
	@NotNull(message = "旧密码不能为空")
	private String oldPassword;

	/**
	 * 新的密码
	 */
	@NotNull(message = "新密码不能为空")
	@Size(min = 5, message = "密码不能少于5位")
	private String newPassword;

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	@AComment(comment = "密码不能少于6位")
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
